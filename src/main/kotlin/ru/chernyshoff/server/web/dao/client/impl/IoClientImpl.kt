package ru.chernyshoff.server.web.dao.client.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.chernyshoff.server.web.dao.client.IoClient
import ru.chernyshoff.server.web.dao.client.mapper.toRequest
import ru.chernyshoff.server.web.dao.client.mapper.toTrace
import ru.chernyshoff.server.web.dao.client.model.TraceResponse
import ru.chernyshoff.server.web.domain.Trace

@Component
class IoClientImpl(
    private val restTemplate: RestTemplate,
    @Value($$"${app.io.host}") private val ioHost: String
) : IoClient {

    override fun trace(trace: Trace): Trace =
        restTemplate.postForObject(
            "$ioHost/api/io/trace",
            trace.toRequest(),
            TraceResponse::class.java
        )!!.toTrace()
}