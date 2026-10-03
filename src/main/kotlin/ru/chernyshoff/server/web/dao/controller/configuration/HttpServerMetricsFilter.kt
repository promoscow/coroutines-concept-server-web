package ru.chernyshoff.server.web.dao.controller.configuration

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.atomic.LongAdder

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class HttpServerMetricsFilter(
    private val meterRegistry: MeterRegistry
) : OncePerRequestFilter() {

    private val inFlight = LongAdder()

    init {
        Gauge.builder(ACTIVE_METRIC_NAME, inFlight) { it.sum().toDouble() }
            .description("HTTP requests currently being processed")
            .register(meterRegistry)
        listOf(SUCCESS, CLIENT_ERROR, SERVER_ERROR).forEach { statusGroup ->
            meterRegistry.counter(
                COMPLETED_METRIC_NAME,
                listOf(Tag.of("status_group", statusGroup))
            )
        }
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        request.requestURI.startsWith(ACTUATOR_PREFIX)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        inFlight.increment()
        var failed = false
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            failed = true
            throw e
        } finally {
            record(statusGroup(response.status, failed))
            inFlight.decrement()
        }
    }

    private fun record(statusGroup: String) {
        meterRegistry.counter(
            COMPLETED_METRIC_NAME,
            listOf(Tag.of("status_group", statusGroup))
        ).increment()
    }

    private fun statusGroup(status: Int, failed: Boolean): String {
        if (failed && status < 400) {
            return SERVER_ERROR
        }
        return when (status) {
            in 200..299 -> SUCCESS
            in 400..499 -> CLIENT_ERROR
            else -> SERVER_ERROR
        }
    }

    companion object {
        const val COMPLETED_METRIC_NAME = "app.http.server.requests.completed"
        const val ACTIVE_METRIC_NAME = "app.http.server.requests.active"
        const val SUCCESS = "2xx"
        const val CLIENT_ERROR = "4xx"
        const val SERVER_ERROR = "5xx"
        private const val ACTUATOR_PREFIX = "/actuator"
    }
}
