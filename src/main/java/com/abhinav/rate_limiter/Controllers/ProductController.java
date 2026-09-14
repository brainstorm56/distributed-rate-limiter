package com.abhinav.rate_limiter.Controllers;

import com.abhinav.rate_limiter.RateLimitResult;
import com.abhinav.rate_limiter.RateLimiter;
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
    public ProductController(RateLimiter rateLimiter)
    {
        this.rateLimiter = rateLimiter;
    }
    @GetMapping("/products")
    public ResponseEntity<String> getProduct(@RequestHeader("X-API-KEY") String apiKey)
    {
        RateLimitResult result = rateLimiter.allowRequest(apiKey);
        if(result == null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API key");
        }
        if(!result.isAllowed())
        {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-RateLimit-Limit", String.valueOf(result.getCapacity()))
                    .header("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()))
                    .header("Retry-After", String.valueOf((int)Math.ceil(result.getRetryAfter())))
                    .body("Rate limit exceeded");
        }
       return ResponseEntity
               .ok()
               .header("X-RateLimit-Limit", String.valueOf(result.getCapacity()))
               .header("X-RateLimit-Remaining", String.valueOf(result.getRemainingTokens()))
               .body("product");
    }
}
