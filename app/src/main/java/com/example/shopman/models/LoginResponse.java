package com.example.shopman.models;

public class LoginResponse {
    private String message;
    private int status;
    private LoginMetadata metadata;

    public LoginMetadata getMetadata()
    {
        return metadata;
    }
}
