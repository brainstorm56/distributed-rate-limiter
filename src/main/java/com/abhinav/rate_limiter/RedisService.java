package com.abhinav.rate_limiter;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;
import java.time.Instant;
import java.util.Map;

@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisScript<List> tokenBucketScript;

    public RedisService(RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/token_bucket.lua"));
        script.setResultType(List.class);

        this.tokenBucketScript = script;
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void setHashValue(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public String getHashValue(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    public void saveClientConfig(
            String clientId,
            RateLimitConfig config) {

        String key = "rate-limit:" + clientId;

        redisTemplate.opsForHash().put(
                key,
                "capacity",
                String.valueOf(config.getCapacity())
        );

        redisTemplate.opsForHash().put(
                key,
                "refillRate",
                String.valueOf(config.getRefillRate())
        );

        redisTemplate.opsForHash().put(
                key,
                "currentTokens",
                String.valueOf(config.getCapacity())
        );

        redisTemplate.opsForHash().put(
                key,
                "lastRefillTime",
                String.valueOf(Instant.now().getEpochSecond())
        );
    }
    public Map<Object, Object> getClientState(String clientId) {
        return redisTemplate.opsForHash()
                .entries("rate-limit:" + clientId);
    }

    public List<Long> tryConsume(
            String clientId) {

        String key = "rate-limit:" + clientId;

        long now = Instant.now().getEpochSecond();

        List<Long> result = redisTemplate.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                String.valueOf(now)
        );
        return result;
    }
}