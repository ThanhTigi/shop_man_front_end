package com.example.shopman.models.auth;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    @SerializedName("message")
    private String message;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("status")
    private int status;

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Metadata getMetadata() {
        return metadata;
    }
}