package com.example.shopman.models.signup;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private SignUpMetadata metaData;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public SignUpMetadata getMetaData() {
        return metaData;
    }
}