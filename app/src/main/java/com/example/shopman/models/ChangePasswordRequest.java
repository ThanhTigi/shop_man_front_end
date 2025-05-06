package com.example.shopman.models;

public class ChangePasswordRequest {
    private String resetToken;
    private String newPassword;
    private String confirmPassword;

    public ChangePasswordRequest(String resetToken, String newPassword, String confirmPassword) {
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
