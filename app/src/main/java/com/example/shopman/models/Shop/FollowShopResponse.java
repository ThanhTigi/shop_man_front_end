package com.example.shopman.models.Shop;

import com.google.gson.annotations.SerializedName;

public class FollowShopResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private FollowShopMetadata metadata;

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

    public FollowShopMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FollowShopMetadata metadata) {
        this.metadata = metadata;
    }

    public static class FollowShopMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private FollowMetadata metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public FollowMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(FollowMetadata metadata) {
            this.metadata = metadata;
        }
    }

    public static class FollowMetadata {
        @SerializedName("success")
        private boolean success;

        @SerializedName("message")
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}