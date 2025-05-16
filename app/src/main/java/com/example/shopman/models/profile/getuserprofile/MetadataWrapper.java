package com.example.shopman.models.profile.getuserprofile;

import com.google.gson.annotations.SerializedName;

public class MetadataWrapper {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private UserProfileMetadata metadata;

    public String getMessage() {
        return message;
    }

    public UserProfileMetadata getMetadata() {
        return metadata;
    }
}