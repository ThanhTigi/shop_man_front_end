package com.example.shopman.models.profile.updateuserprofile;

public class UpdateProfileUser {
    private String name;
    private String phone;
    private String avatar;

    public UpdateProfileUser(String avatar, String phone, String name) {
        this.avatar = avatar;
        this.phone = phone;
        this.name = name;
    }
}
