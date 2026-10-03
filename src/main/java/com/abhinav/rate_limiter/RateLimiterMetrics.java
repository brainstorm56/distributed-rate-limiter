package com.abhinav.rate_limiter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RateLimiterMetrics {

    private final Counter allowedRequests;
    private final Counter rejectedRequests;

    public RateLimiterMetrics(MeterRegistry meterRegistry) {
        allowedRequests = Counter.builder("rate_limiter_requests")
                .tag("result", "allowed")
                .description("Number of requests allowed by the rate limiter")
                .register(meterRegistry);

        rejectedRequests = Counter.builder("rate_limiter_requests")
                .tag("result", "rejected")
                .description("Number of requests rejected by the rate limiter")
                .register(meterRegistry);
    }

    public void recordAllowed() {
        allowedRequests.increment();
    }

    public void recordRejected() {
        rejectedRequests.increment();
    }
}