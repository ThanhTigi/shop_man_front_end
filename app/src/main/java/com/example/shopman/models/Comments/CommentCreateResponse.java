package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

public class CommentCreateResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private Comment comment; // Sử dụng Comment trực tiếp thay vì danh sách

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Comment getMetadata() {
            return comment;
        }

        public void setMetadata(Comment comment) {
            this.comment = comment;
        }
    }
}