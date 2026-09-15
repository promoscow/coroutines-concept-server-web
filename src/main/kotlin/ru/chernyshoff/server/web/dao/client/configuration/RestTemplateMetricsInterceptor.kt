package ru.chernyshoff.server.web.dao.client.configuration

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException

@Component
class RestTemplateMetricsInterceptor(
    private val meterRegistry: MeterRegistry
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse =
        try {
            execution.execute(request, body).also { record(it.statusCode.value()) }
        } catch (e: HttpStatusCodeException) {
            record(e.statusCode.value())
            throw e
        }

    private fun record(status: Int) {
        meterRegistry.counter(
            METRIC_NAME,
            listOf(Tag.of("status", status.toString()))
        ).increment()
    }

    companion object {
        const val METRIC_NAME = "app.io.client.http.requests"
    }
}
