package com.example.shopman.models.profile.updateuserprofile;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileAddress {
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

    public UpdateProfileAddress(String addressType, int pincode, String address, String city, String country) {
        this.addressType = addressType;
        this.pincode = pincode;
        this.address = address;
        this.city = city;
        this.country = country;
    }
}