package ru.chernyshoff.server.web.dao.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.chernyshoff.server.web.service.IoService

@RestController
@RequestMapping("/server/io")
class IoController(
    private val service: IoService
) {

    @GetMapping("/trace/{traceId}")
    fun trace(@PathVariable traceId: String): String = service.trace(traceId)
}