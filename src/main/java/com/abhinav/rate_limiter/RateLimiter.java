package com.abhinav.rate_limiter;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
@Component
public class RateLimiter {

    private final RedisService redisService;

    public RateLimiter(RedisService redisService)
    {
        this.redisService = redisService;
    }

    public boolean configureClient(String clientId, RateLimitConfig config)
    {
        redisService.saveClientConfig(clientId, config);
        return true;
    }

    public RateLimitResult allowRequest(String clientId)
    {
        List<Long> result = redisService.tryConsume(
                clientId
        );
        if (result.get(0) == -1) {
            return null;
        }
        boolean allowed = result.get(0) == 1;
        int remainingTokens = result.get(1).intValue();
        int capacity = result.get(2).intValue();
        double retryAfter = result.get(3);

        return new RateLimitResult(
                allowed,
                remainingTokens,
                capacity,
                retryAfter
        );
    }
}
