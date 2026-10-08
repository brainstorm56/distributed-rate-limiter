package com.abhinav.rate_limiter.Controllers;

import com.abhinav.rate_limiter.RateLimitConfig;
import com.abhinav.rate_limiter.RateLimiter;
import com.abhinav.rate_limiter.dto.RateLimitConfigRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/rate-limits")
public class RateLimitController {

    @Value("${rate-limiter.admin-api-key}")
    private String adminApiKey;

    private final RateLimiter rateLimiter;

    public RateLimitController(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<String> configureClient(
            @PathVariable String clientId,
            @RequestHeader(value = "X-ADMIN-API-KEY", required = false)
            String providedAdminApiKey,
            @Valid @RequestBody RateLimitConfigRequest request) {

        if (providedAdminApiKey == null
                || !providedAdminApiKey.equals(adminApiKey)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin API key");
        }

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
