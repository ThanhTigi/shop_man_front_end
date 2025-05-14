package com.example.shopman.models.profile.updateuserprofile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileUser {
    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("avatar")
    private String avatar;

    public UpdateProfileUser(String name, String phone, String avatar) {
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
    }
}