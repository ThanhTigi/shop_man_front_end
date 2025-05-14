package com.example.shopman.models.profile.getuserprofile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserProfileMetadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private UserProfileMetadata userProfileMetaData;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("google_id")
    private String googleId;

    @SerializedName("phone")
    private String phone;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("balance")
    private String balance;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("address")
    private List<Address> address;

    public String getMessage() {
        return message;
    }

    public UserProfileMetadata getUserProfileMetaData() {
        return userProfileMetaData;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public List<Address> getAddress() {
        return address;
    }
}