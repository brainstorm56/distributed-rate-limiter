package com.abhinav.rate_limiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RateLimiterConcurrencyTest {

    @Autowired
    private RateLimiter rateLimiter;

    @BeforeEach
    void setup() {
        rateLimiter.configureClient(
                "concurrency-test",
                new RateLimitConfig(100, 0.01)
        );
    }

    @Test
    void shouldAllowExactly100ConcurrentRequests() throws Exception {

        int numberOfRequests = 1000;

        ExecutorService executor =
                Executors.newFixedThreadPool(100);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        AtomicInteger allowedRequests =
                new AtomicInteger();

        for (int i = 0; i < numberOfRequests; i++) {

            executor.submit(() -> {
                try {
                    startLatch.await();

                    RateLimitResult result =
                            rateLimiter.allowRequest("concurrency-test");

                    if (result.isAllowed()) {
                        allowedRequests.incrementAndGet();
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Release all threads at approximately the same time.
        startLatch.countDown();

        executor.shutdown();

        boolean finished =
                executor.awaitTermination(30, TimeUnit.SECONDS);

        if (!finished) {
            executor.shutdownNow();
        }

        assertEquals(100, allowedRequests.get());
    }
}