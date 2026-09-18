package com.abhinav.rate_limiter.dto;

public class RateLimitResponse {

    private String message;
    private int capacity;
    private int remainingTokens;
    private double retryAfter;

    public RateLimitResponse(
            String message,
            int capacity,
            int remainingTokens,
            double retryAfter) {
        this.message = message;
        this.capacity = capacity;
        this.remainingTokens = remainingTokens;
        this.retryAfter = retryAfter;
    }

    public String getMessage() {
        return message;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRemainingTokens() {
        return remainingTokens;
    }

    public double getRetryAfter() {
        return retryAfter;
    }
}