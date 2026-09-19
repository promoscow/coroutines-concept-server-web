package ru.chernyshoff.server.web.dao.client.configuration

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import java.io.IOException
import java.util.concurrent.atomic.LongAdder

@Component
class RestTemplateMetricsInterceptor(
    private val meterRegistry: MeterRegistry
) : ClientHttpRequestInterceptor {

    private val inFlight = LongAdder()

    init {
        Gauge.builder(ACTIVE_METRIC_NAME, inFlight) { it.sum().toDouble() }
            .description("Threads that sent a request to the io service and are awaiting a response")
            .register(meterRegistry)
    }

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        inFlight.increment()
        return try {
            execution.execute(request, body).also { record(it.statusCode.value().toString()) }
        } catch (e: HttpStatusCodeException) {
            record(e.statusCode.value().toString())
            throw e
        } catch (e: IOException) {
            record(IO_ERROR_STATUS, e.javaClass.simpleName)
            throw e
        } finally {
            inFlight.decrement()
        }
    }

    private fun record(status: String, exception: String = NO_EXCEPTION) {
        meterRegistry.counter(
            METRIC_NAME,
            listOf(Tag.of("status", status), Tag.of("exception", exception))
        ).increment()
    }

    companion object {
        const val METRIC_NAME = "app.io.client.http.requests"
        const val ACTIVE_METRIC_NAME = "app.io.client.http.requests.active"
        const val IO_ERROR_STATUS = "io-error"
        const val NO_EXCEPTION = "none"
    }
}
