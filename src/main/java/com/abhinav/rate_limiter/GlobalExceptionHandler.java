package com.abhinav.rate_limiter;

import com.abhinav.rate_limiter.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimiterUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimiterUnavailable(
            RateLimiterUnavailableException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                "RATE_LIMITER_UNAVAILABLE",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}