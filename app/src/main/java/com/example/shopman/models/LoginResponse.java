package com.example.shopman.models;

public class LoginResponse {
    private String message;
    private UserMetadata userMetadata;

    public String getMessage() {
        return message;
    }

    public UserMetadata getMetadata() {
        return userMetadata;
    }
}
