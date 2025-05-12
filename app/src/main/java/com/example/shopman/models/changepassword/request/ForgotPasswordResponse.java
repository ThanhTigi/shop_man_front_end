package com.example.shopman.models.changepassword.request;

public class ForgotPasswordResponse {
    private String message;
    private int status;
    private ForgotPasswordMetadata metaData;

    public ForgotPasswordMetadata getMetaData() {
        return metaData;
    }

    public void setMetaData(ForgotPasswordMetadata metaData) {
        this.metaData = metaData;
    }
}
