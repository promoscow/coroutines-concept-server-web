package ru.chernyshoff.server.web

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application {

	private val logger = KotlinLogging.logger { this::class.java }

	@PostConstruct
	fun init() {
		val runtime = Runtime.getRuntime()
		logger.info { "Max memory (bytes): ${runtime.maxMemory()}" }
		logger.info { "Processor cores: ${runtime.availableProcessors()}" }
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
