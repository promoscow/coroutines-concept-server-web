package ru.chernyshoff.server.web.dao.controller.model

import ru.chernyshoff.server.web.dao.controller.model.type.ServiceTypeDto
import java.time.OffsetDateTime

data class TraceResponse(
    val traceId: String,
    val timestamp: OffsetDateTime,
    val responseService: ServiceTypeDto
)
