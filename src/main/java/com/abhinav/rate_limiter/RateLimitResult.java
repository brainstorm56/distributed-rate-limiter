package com.abhinav.rate_limiter;

public class RateLimitResult {
    private final boolean allowed;
    private final int remainingTokens;
    private final int capacity;
    private final double retryAfter;

    public RateLimitResult(boolean allowed, int remainingTokens, int capacity, double retryAfter)
    {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.capacity = capacity;
        this.retryAfter = retryAfter;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemainingTokens() {
        return remainingTokens;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getRetryAfter() {
        return retryAfter;
    }
}
