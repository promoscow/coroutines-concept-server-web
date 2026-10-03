package ru.chernyshoff.server.web.dao.controller.configuration

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class HttpServerMetricsFilterTest {

    private val registry = SimpleMeterRegistry()
    private val filter = HttpServerMetricsFilter(registry)

    @Test
    fun records2xxAndClearsInFlight() {
        doFilter(status = 200)

        assertEquals(1.0, completed("2xx"))
        assertEquals(0.0, completed("4xx"))
        assertEquals(0.0, completed("5xx"))
        assertEquals(0.0, active())
    }

    @Test
    fun records4xx() {
        doFilter(status = 404)

        assertEquals(1.0, completed("4xx"))
    }

    @Test
    fun records5xx() {
        doFilter(status = 500)

        assertEquals(1.0, completed("5xx"))
    }

    @Test
    fun mapsUnexpectedStatusTo5xx() {
        doFilter(status = 302)

        assertEquals(1.0, completed("5xx"))
        assertEquals(0.0, completed("2xx"))
        assertEquals(0.0, completed("4xx"))
    }

    @Test
    fun recordsExceptionWithoutErrorStatusAs5xx() {
        assertThrows(IllegalStateException::class.java) {
            doFilter { throw IllegalStateException("boom") }
        }

        assertEquals(1.0, completed("5xx"))
        assertEquals(0.0, active())
    }

    @Test
    fun keeps4xxWhenExceptionFollowsClientErrorStatus() {
        assertThrows(IllegalStateException::class.java) {
            doFilter { response ->
                response.status = 404
                throw IllegalStateException("boom")
            }
        }

        assertEquals(1.0, completed("4xx"))
        assertEquals(0.0, completed("5xx"))
    }

    @Test
    fun skipsActuator() {
        doFilter(path = "/actuator/prometheus", status = 200)

        assertEquals(0.0, completed("2xx"))
        assertEquals(0.0, completed("4xx"))
        assertEquals(0.0, completed("5xx"))
        assertEquals(0.0, active())
    }

    @Test
    fun tracksInFlightDuringRequest() {
        doFilter { _ ->
            assertEquals(1.0, active())
        }
        assertEquals(0.0, active())
    }

    private fun doFilter(
        path: String = "/server/io/trace",
        status: Int = 200,
        body: (HttpServletResponse) -> Unit = { it.status = status }
    ) {
        val request = MockHttpServletRequest("GET", path)
        val response = MockHttpServletResponse()
        val chain = FilterChain { _, servletResponse ->
            body(servletResponse as HttpServletResponse)
        }
        filter.doFilter(request, response, chain)
    }

    private fun completed(statusGroup: String): Double =
        registry.counter(
            HttpServerMetricsFilter.COMPLETED_METRIC_NAME,
            "status_group",
            statusGroup
        ).count()

    private fun active(): Double =
        registry.find(HttpServerMetricsFilter.ACTIVE_METRIC_NAME).gauge()?.value() ?: 0.0
}
