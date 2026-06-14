package ru.chernyshoff.server.web.service.impl

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.chernyshoff.server.web.dao.client.IoClient
import ru.chernyshoff.server.web.domain.Trace
import ru.chernyshoff.server.web.domain.type.ServiceType
import ru.chernyshoff.server.web.service.IoService

@Service
class IoServiceImpl(
    private val client: IoClient,
    @Value($$"${app.service-prefix}") private val servicePrefix: String
) : IoService {

    override fun trace(trace: Trace): Trace =
        "${servicePrefix}.${RandomStringUtils.secure().nextAlphanumeric(6)}"
            .let { newTraceId ->
                val newTrace = Trace(
                    traceId = "${trace.traceId}-$newTraceId",
                    service = ServiceType.SERVER
                )
                val ioTrace = client.trace(newTrace)
                Trace(
                    traceId = ioTrace.traceId,
                    service = ServiceType.SERVER
                )
            }
}