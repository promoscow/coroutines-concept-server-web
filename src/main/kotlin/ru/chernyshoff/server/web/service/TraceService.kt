package ru.chernyshoff.server.web.service

import ru.chernyshoff.server.web.domain.Trace

interface IoService {

    fun trace(trace: Trace): Trace
}