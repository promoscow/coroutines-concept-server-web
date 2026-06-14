package ru.chernyshoff.server.web.dao.client

import ru.chernyshoff.server.web.domain.Trace

interface IoClient {

    fun trace(trace: Trace): Trace
}