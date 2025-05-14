package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

public class OTPRequest {
    @SerializedName("otp")
    private String otp;

    public OTPRequest(String otp) {
        this.otp = otp;
    }
}