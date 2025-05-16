package com.example.shopman.models.profile.getuserprofile;

import com.google.gson.annotations.SerializedName;

public class GetUserProfileResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private MetadataWrapper metadata; // Sử dụng MetadataWrapper thay vì UserProfileMetadata

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public MetadataWrapper getMetadata() {
        return metadata;
    }
}