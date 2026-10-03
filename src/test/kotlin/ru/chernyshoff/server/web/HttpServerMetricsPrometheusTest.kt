package ru.chernyshoff.server.web

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class HttpServerMetricsPrometheusTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun prometheusExposesHttpServerRequestMetrics() {
        mockMvc.perform(get("/actuator/prometheus"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("app_http_server_requests_active")))
            .andExpect(content().string(containsString("app_http_server_requests_completed_total")))
            .andExpect(content().string(containsString("""status_group="2xx"""")))
            .andExpect(content().string(containsString("""status_group="4xx"""")))
            .andExpect(content().string(containsString("""status_group="5xx"""")))
    }
}
