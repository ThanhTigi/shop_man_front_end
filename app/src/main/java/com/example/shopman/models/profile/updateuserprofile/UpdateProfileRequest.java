package com.example.shopman.models.profile.updateuserprofile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("User")
    private UpdateProfileUser user;

    @SerializedName("Address")
    private UpdateProfileAddress address;

    public UpdateProfileRequest(UpdateProfileUser user, UpdateProfileAddress address) {
        this.user = user;
        this.address = address;
    }
}