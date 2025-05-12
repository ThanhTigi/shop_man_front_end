package com.example.shopman.models.checkotp;

public class CheckOTPResponse {
    private String message;
    private int status;
    private CheckOTPMetadata metadata;

    public CheckOTPMetadata getMetadata() {
        return metadata;
    }
}
