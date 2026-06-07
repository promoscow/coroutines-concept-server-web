package ru.chernyshoff.server.web.dao.client.impl

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.chernyshoff.server.web.dao.client.IoClient

@Component
class IoClientImpl(
    private val restTemplate: RestTemplate
) : IoClient {

    override fun trace(traceId: String): String =
        restTemplate.getForObject(
            "http://io:8021/api/io/trace/{traceId}",
            String::class.java,
            traceId
        )!!
}