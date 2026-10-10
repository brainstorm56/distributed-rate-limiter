package com.abhinav.rate_limiter.Controllers;

import com.abhinav.rate_limiter.RateLimitResult;
import com.abhinav.rate_limiter.RateLimiter;
import com.abhinav.rate_limiter.dto.ApiErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final RateLimiter rateLimiter;
    @Value("${INSTANCE_ID:unknown}")
    private String instanceId;
    public ProductController(RateLimiter rateLimiter)
    {
        this.rateLimiter = rateLimiter;
    }
    @GetMapping("/products")
    public ResponseEntity<?> getProduct(@RequestHeader(value = "X-API-KEY", required = false) String apiKey)
    {
        if(apiKey == null || apiKey.isBlank())
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponse(
                            "API_KEY_MISSING",
                            "API key is required"
                    ));
        }
        RateLimitResult result = rateLimiter.allowRequest(apiKey);
        if(result == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                    "INVALID_API_KEY",
                    "Invalid API key"
            ));
        }
        if(!result.isAllowed())
        {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-RateLimit-Limit", String.valueOf(result.getCapacity()))
                    .header("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()))
                    .header("Retry-After", String.valueOf((int)Math.ceil(result.getRetryAfter())))
                    .body(new ApiErrorResponse(
                            "RATE_LIMIT_EXCEEDED",
                            "Rate limit exceeded"
                    ));
        }
       return ResponseEntity
               .ok()
               .header("X-RateLimit-Limit", String.valueOf(result.getCapacity()))
               .header("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()))
               .body("product from " + instanceId);
    }
}
