package com.abhinav.rate_limiter.dto;

public class ApiErrorResponse {
    private String error;
    private String message;

    public ApiErrorResponse(String error, String message){
        this.error = error;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
