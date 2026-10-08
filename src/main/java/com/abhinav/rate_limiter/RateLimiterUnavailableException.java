package com.abhinav.rate_limiter;

public class RateLimiterUnavailableException extends RuntimeException {

    public RateLimiterUnavailableException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}