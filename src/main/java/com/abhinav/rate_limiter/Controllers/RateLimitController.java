package com.abhinav.rate_limiter.Controllers;

import com.abhinav.rate_limiter.RateLimitConfig;
import com.abhinav.rate_limiter.RateLimiter;
import com.abhinav.rate_limiter.dto.RateLimitConfigRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limits")
public class RateLimitController {

    private final RateLimiter rateLimiter;

    public RateLimitController(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<String> configureClient(
            @PathVariable String clientId,
            @Valid @RequestBody RateLimitConfigRequest request) {

        rateLimiter.configureClient(
                clientId,
                new RateLimitConfig(
                        request.getCapacity(),
                        request.getRefillRate()
                )
        );

        return ResponseEntity.ok("Client configured");
    }
}
