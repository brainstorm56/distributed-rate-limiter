package com.abhinav.rate_limiter;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
@Component
public class RateLimiter {

    private final RedisService redisService;
    private final RateLimiterMetrics metrics;

    public RateLimiter(RedisService redisService, RateLimiterMetrics metrics)
    {
        this.redisService = redisService;
        this.metrics = metrics;
    }

    public boolean configureClient(String clientId, RateLimitConfig config)
    {
        redisService.saveClientConfig(clientId, config);
        return true;
    }

    public RateLimitResult allowRequest(String clientId)
    {
        try {
            List<Long> result = redisService.tryConsume(
                    clientId
            );
            if (result.get(0) == -1) {
                return null;
            }
            boolean allowed = result.get(0) == 1;
            if(allowed){
                metrics.recordAllowed();
            }
            else {
                metrics.recordRejected();
            }
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
        catch (Exception e) {
            // Redis is unavailable
            throw new RateLimiterUnavailableException(
                    "Rate limiter is temporarily unavailable", e);
        }
    }
}
