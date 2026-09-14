package com.abhinav.rate_limiter;

import java.time.Duration;
import java.time.Instant;


public class TokenBucket {
    private int capacity;
    private int currentTokens;
    private Instant lastRefillTime;
    private double refillRate;

    public TokenBucket(int capacity, double refillRate)
    {
        if (capacity <= 0 || refillRate <= 0) {
            throw new IllegalArgumentException("capacity and refillRate should both be greater than zero.");
        }

        this.capacity = capacity;
        this.refillRate = refillRate;
        this.currentTokens = capacity;
        this.lastRefillTime = Instant.now();
    }

    public synchronized RateLimitResult tryConsume()
    {
        Instant now = Instant.now();
        double timeElapsedFromLastRefill = Duration.between(lastRefillTime, now).toNanos()/1_000_000_000.0;
        long potentialTokens = (long)(timeElapsedFromLastRefill*refillRate);
        int availableSpace = capacity - currentTokens;
        int tokensToBeAdded = (int)Math.min(availableSpace, potentialTokens);
        currentTokens += tokensToBeAdded;
        if(currentTokens == capacity)
        {
            lastRefillTime = now;
        }
        else
        {
            lastRefillTime = lastRefillTime.plusNanos((long)((potentialTokens /refillRate)*1_000_000_000));
        }
//        System.out.println(currentTokens + " " + lastRefillTime);
        if(currentTokens >=1)
        {
            currentTokens--;
            return new RateLimitResult(true, currentTokens, capacity, 0.0 );
        }
        return new RateLimitResult(false, currentTokens, capacity, 1/refillRate );
    }

    public double getRefillRate() {
        return refillRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentToken() {
        return currentTokens;
    }

    public Instant getLastRefillTime() {
        return lastRefillTime;
    }
}
