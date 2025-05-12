package com.example.shopman.models.changepassword.response;

import com.example.shopman.models.login.LoginMetadata;

public class ChangePasswordResponse {
    private String message;
    private int status;
    private LoginMetadata metadata;

    public LoginMetadata getMetadata() {
        return metadata;
    }
}
