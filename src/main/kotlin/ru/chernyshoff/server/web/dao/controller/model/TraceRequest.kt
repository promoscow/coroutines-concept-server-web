package ru.chernyshoff.server.web.dao.controller.model

import ru.chernyshoff.server.web.dao.controller.model.type.ServiceTypeDto
import java.time.OffsetDateTime

data class TraceRequest(
    val traceId: String,
    val timestamp: OffsetDateTime,
    val requestService: ServiceTypeDto
)