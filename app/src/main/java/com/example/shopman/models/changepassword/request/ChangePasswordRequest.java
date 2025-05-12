package com.example.shopman.models.changepassword.request;

public class ChangePasswordRequest {
    private String resetToken;
    private String newPassword;
    private String confirmedPassword;

    public ChangePasswordRequest(String resetToken, String newPassword, String confirmPassword) {
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmedPassword = confirmPassword;
    }
}
