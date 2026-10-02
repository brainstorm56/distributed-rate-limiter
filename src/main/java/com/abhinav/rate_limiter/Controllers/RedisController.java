package com.abhinav.rate_limiter.Controllers;

import com.abhinav.rate_limiter.RedisService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisService redisService;

    public RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/test")
    public String setValue(
            @RequestParam String key,
            @RequestParam String value) {

        redisService.set(key, value);
        return "Value stored";
    }

    @GetMapping("/test")
    public String getValue(@RequestParam String key) {
        return redisService.get(key);
    }
    @PostMapping("/hash")
    public String setHash() {
        redisService.setHashValue("rate-limit:client-a", "capacity", "3");
        redisService.setHashValue("rate-limit:client-a", "currentTokens", "2");
        redisService.setHashValue("rate-limit:client-a", "refillRate", "0.1");
        redisService.setHashValue("rate-limit:client-a", "lastRefillTime", Instant.now().toString());

        return "Hash stored";
    }
}