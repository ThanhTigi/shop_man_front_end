package com.example.shopman.models.changepassword.response;

import com.example.shopman.models.login.InnerMetadata;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ChangePasswordMetadata metaData;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ChangePasswordMetadata getMetaData() {
        return metaData;
    }

    public static class ChangePasswordMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private InnerMetadata innerMetadata;

        public String getMessage() {
            return message;
        }

        public InnerMetadata getMetadata() {
            return innerMetadata;
        }
    }
}