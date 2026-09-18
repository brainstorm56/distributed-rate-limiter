package com.abhinav.rate_limiter.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RateLimitConfigRequest {

    @NotNull
    @Min(1) 
    private Integer capacity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double refillRate;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getRefillRate() {
        return refillRate;
    }

    public void setRefillRate(Double refillRate) {
        this.refillRate = refillRate;
    }
}