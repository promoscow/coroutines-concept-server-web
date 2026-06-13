package ru.chernyshoff.server.web.dao.client.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.chernyshoff.server.web.dao.client.IoClient

@Component
class IoClientImpl(
    private val restTemplate: RestTemplate,
    @Value($$"${app.io.host}") private val ioHost: String
) : IoClient {

    override fun trace(traceId: String): String =
        restTemplate.getForObject(
            "$ioHost/api/io/trace/{traceId}",
            String::class.java,
            traceId
        )!!
}