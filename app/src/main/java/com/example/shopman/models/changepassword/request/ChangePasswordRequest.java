package com.example.shopman.models.changepassword.request;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("resetToken")
    private String resetToken;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("confirmedPassword")
    private String confirmedPassword;

    public ChangePasswordRequest(String resetToken, String newPassword, String confirmedPassword) {
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmedPassword = confirmedPassword;
    }
}