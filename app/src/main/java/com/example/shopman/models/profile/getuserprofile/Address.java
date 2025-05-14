package com.example.shopman.models.profile.getuserprofile;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("id")
    private int id;

    @SerializedName("UserId")
    private int userId;

    @SerializedName("address_type")
    private String addressType;

    @SerializedName("pincode")
    private int pincode;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getAddressType() {
        return addressType;
    }

    public int getPincode() {
        return pincode;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}