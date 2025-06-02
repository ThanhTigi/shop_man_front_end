package com.example.shopman.models.auth;

import com.google.gson.annotations.SerializedName;

public class Metadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private InnerMetadata metadata;

    public String getMessage() {
        return message;
    }

    public InnerMetadata getMetadata() {
        return metadata;
    }
}