package com.abhinav.rate_limiter;

public class ClientRateLimit {
    private TokenBucket tokenBucket;
    private RateLimitConfig rateLimitConfig;

    public ClientRateLimit(RateLimitConfig rateLimitConfig)
    {
        this.rateLimitConfig = rateLimitConfig;
        this.tokenBucket = new TokenBucket(rateLimitConfig.getCapacity(), rateLimitConfig.getRefillRate());
    }
    public RateLimitResult allowRequest()
    {
        return tokenBucket.tryConsume();
    }
}
