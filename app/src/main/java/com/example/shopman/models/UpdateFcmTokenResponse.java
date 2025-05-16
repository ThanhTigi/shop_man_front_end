package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

public class UpdateFcmTokenResponse {
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

        @SerializedName("metadata")
        private int[] metadata; // Mảng int dựa trên [1] trong JSON

        public String getMessage() {
            return message;
        }

        public int[] getMetadata() {
            return metadata;
        }
    }
}