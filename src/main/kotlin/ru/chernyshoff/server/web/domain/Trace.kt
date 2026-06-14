package ru.chernyshoff.server.web.domain

import ru.chernyshoff.server.web.domain.type.ServiceType

data class Trace(
    val traceId: String,
    val service: ServiceType
)
