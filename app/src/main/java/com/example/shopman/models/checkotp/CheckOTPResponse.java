package com.example.shopman.models.checkotp;

import com.google.gson.annotations.SerializedName;

public class CheckOTPResponse {
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
        private InnerMetadata innerMetadata;

        public String getMessage() {
            return message;
        }

        public InnerMetadata getInnerMetadata() {
            return innerMetadata;
        }
    }

    public static class InnerMetadata {
        @SerializedName("resetToken")
        private String resetToken;

        public String getResetToken() {
            return resetToken;
        }
    }
}