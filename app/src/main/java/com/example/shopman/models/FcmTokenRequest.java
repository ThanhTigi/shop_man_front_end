package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

public class FcmTokenRequest {


    @SerializedName("fcmToken")
    private String fcmToken;

    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}