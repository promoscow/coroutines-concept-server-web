package ru.chernyshoff.server.web.dao.client

interface IoClient {

    fun trace(traceId: String): String
}