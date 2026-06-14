package ru.chernyshoff.server.web.dao.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.chernyshoff.server.web.dao.controller.mapper.toResponse
import ru.chernyshoff.server.web.dao.controller.mapper.toTrace
import ru.chernyshoff.server.web.dao.controller.model.TraceRequest
import ru.chernyshoff.server.web.dao.controller.model.TraceResponse
import ru.chernyshoff.server.web.service.IoService

@RestController
@RequestMapping("/server/io")
class IoController(
    private val service: IoService
) {

    @PostMapping("/trace")
    fun trace(@RequestBody request: TraceRequest): TraceResponse =
        request.toTrace().let { service.trace(it) }.toResponse()
}