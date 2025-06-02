package com.example.shopman.models.category;

import com.google.gson.annotations.SerializedName;

public class CategoryResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private CategoryMetadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public CategoryMetadata getMetadata() {
        return metadata;
    }
}