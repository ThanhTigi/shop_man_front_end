package com.example.shopman.models.profile.updateuserprofile;

public class UpdateProfileRequest {
    private UpdateProfileUser User;
    private UpdateProfileAddress Address;

    public UpdateProfileRequest(UpdateProfileUser user, UpdateProfileAddress address) {
        User = user;
        this.Address = address;
    }
}
