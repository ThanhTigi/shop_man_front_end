package com.example.shopman.models.changepassword.request;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

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

    public static class Metadata {
        @SerializedName("message")
        private String message;

        public String getMessage() {
            return message;
        }
    }
}