package com.example.shopman.models.signup;

public class SignUpRequest
{
    private String email;
    private String name;
    private String password;

    public SignUpRequest(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
}
