package ru.chernyshoff.server.web.service

interface IoService {

    fun trace(traceId: String): String
}