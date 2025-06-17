package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

public class DeleteCommentResponse {
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
        private int metadata;

        public String getMessage() {
            return message;
        }

        public int getMetadata() {
            return metadata;
        }
    }
}