package com.abhinav.rate_limiter.config;

import com.abhinav.rate_limiter.RateLimitConfig;
import com.abhinav.rate_limiter.RateLimiter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfiguration {

    public RateLimiterConfiguration(RateLimiter rateLimiter) {

        rateLimiter.configureClient(
                "client-a",
                new RateLimitConfig(4, 2)
        );

        rateLimiter.configureClient(
                "client-b",
                new RateLimitConfig(3, 0.4)
        );
    }
}