package com.abhinav.rate_limiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Value;

@SpringBootTest(properties = {
        "RATE_LIMITER_ADMIN_API_KEY=test-admin-key"
})
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimiter rateLimiter;

    @BeforeEach
    void setup() {
        rateLimiter.configureClient(
                "integration-test",
                new RateLimitConfig(10, 0.01)
        );
    }

    @Test
    void shouldAllowRequestWithValidApiKey() throws Exception {

        mockMvc.perform(
                        get("/api/products")
                                .header("X-API-KEY", "integration-test")
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectRequestWhenRateLimitExceeded() throws Exception {

        // Consume all 10 tokens
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(
                    get("/api/products")
                            .header("X-API-KEY", "integration-test")
            ).andExpect(status().isOk());
        }

        // 11th request should be rejected
        mockMvc.perform(
                        get("/api/products")
                                .header("X-API-KEY", "integration-test")
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                "10",
                                result.getResponse().getHeader("X-RateLimit-Limit")
                        ))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                "0",
                                result.getResponse().getHeader("X-RateLimit-Remaining")
                        ))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertNotNull(
                                result.getResponse().getHeader("Retry-After")
                        ));
    }

    @Test
    void shouldRejectRequestWhenApiKeyIsMissing() throws Exception {

        mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWhenApiKeyIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/api/products")
                                .header("X-API-KEY", "invalid-key")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectConfigurationWithoutAdminApiKey() throws Exception {

        mockMvc.perform(
                post("/api/rate-limits/test-client")
                        .contentType("application/json")
                        .content("""
                            {
                                "capacity": 10,
                                "refillRate": 1
                            }
                            """)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectConfigurationWithInvalidAdminApiKey() throws Exception {

        mockMvc.perform(
                post("/api/rate-limits/test-client")
                        .header("X-ADMIN-API-KEY", "wrong-key")
                        .contentType("application/json")
                        .content("""
                            {
                                "capacity": 10,
                                "refillRate": 1
                            }
                            """)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowConfigurationWithValidAdminApiKey() throws Exception {

        mockMvc.perform(
                post("/api/rate-limits/test-client")
                        .header("X-ADMIN-API-KEY", "test-admin-key")
                        .contentType("application/json")
                        .content("""
                            {
                                "capacity": 10,
                                "refillRate": 1
                            }
                            """)
        ).andExpect(status().isOk());
    }

//    @Test
//    void shouldReturn503WhenRedisIsUnavailable() throws Exception {
//
//        // Stop Redis before running this test manually.
//        // This test documents the expected API behavior.
//
//        mockMvc.perform(
//                        get("/api/products")
//                                .header("X-API-KEY", "redis-failure-test")
//                )
//                .andExpect(status().isServiceUnavailable())
//                .andExpect(jsonPath("$.error")
//                        .value("RATE_LIMITER_UNAVAILABLE"));
//    }
}