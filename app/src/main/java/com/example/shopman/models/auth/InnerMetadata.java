package com.example.shopman.models.auth;

import com.google.gson.annotations.SerializedName;

public class InnerMetadata {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}