package com.abhinav.rate_limiter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class RateLimiter {

    private ConcurrentHashMap<String, ClientRateLimit> clients;

    public RateLimiter()
    {
        this.clients = new ConcurrentHashMap<>();
    }

    public boolean configureClient(String clientId, RateLimitConfig config)
    {
        clients.put(clientId, new ClientRateLimit(config));
        return true;
    }

    public RateLimitResult allowRequest(String clientId)
    {
        ClientRateLimit clientRateLimit = clients.get(clientId);
        if(clientRateLimit == null)
        {
            return null;
        }
        return clientRateLimit.allowRequest();
    }
}
