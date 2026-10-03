package ru.chernyshoff.server.web.dao.client.configuration

import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfiguration(
    private val metricsInterceptor: RestTemplateMetricsInterceptor
) {

    @Bean
    fun clientHttpRequestFactory(): HttpComponentsClientHttpRequestFactory {
        val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(MAX_CONNECTIONS)
            .setMaxConnPerRoute(MAX_CONNECTIONS)
            .setDefaultConnectionConfig(
                ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(30))
                    .build()
            )
            .build()

        val httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setConnectionRequestTimeout(Timeout.ofSeconds(60))
                    .setResponseTimeout(Timeout.ofSeconds(60))
                    .build()
            )
            .evictExpiredConnections()
            .evictIdleConnections(TimeValue.ofSeconds(60))
            .build()

        return HttpComponentsClientHttpRequestFactory(httpClient)
    }

    @Bean
    fun restTemplate(clientHttpRequestFactory: HttpComponentsClientHttpRequestFactory): RestTemplate =
        RestTemplate(clientHttpRequestFactory).apply {
            interceptors.add(metricsInterceptor)
        }

    companion object {
        private const val MAX_CONNECTIONS = 50_000
    }
}
