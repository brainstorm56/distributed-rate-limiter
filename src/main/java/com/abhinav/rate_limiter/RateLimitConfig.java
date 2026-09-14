package com.abhinav.rate_limiter;

public class RateLimitConfig {
    private int capacity;
    private double refillRate;

    public RateLimitConfig(int capacity, double refillRate)
    {
        if(capacity <= 0 || refillRate <=0)
        {
            throw new IllegalArgumentException("capacity and refillRate should both be greater than zero.");
        }
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getRefillRate() {
        return refillRate;
    }
}
