package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private Integer status;

    public String getMessage() {
        return message != null ? message : "Unknown error";
    }

    public Integer getStatus() {
        return status;
    }
}