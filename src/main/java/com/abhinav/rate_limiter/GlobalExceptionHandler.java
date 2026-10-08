package com.abhinav.rate_limiter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimiterUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleRateLimiterUnavailable(
            RateLimiterUnavailableException ex) {

        Map<String, String> response = Map.of(
                "error", "RATE_LIMITER_UNAVAILABLE",
                "message", ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}