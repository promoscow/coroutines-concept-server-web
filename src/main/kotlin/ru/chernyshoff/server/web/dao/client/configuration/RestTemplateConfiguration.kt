package ru.chernyshoff.server.web.dao.client.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfiguration(
    private val metricsInterceptor: RestTemplateMetricsInterceptor
) {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate().apply {
        interceptors.add(metricsInterceptor)
    }
}