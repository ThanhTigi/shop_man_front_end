package com.example.shopman.models.profile.updateuserprofile;

public class UpdateProfileAddress {
    private String address_type;
    private int pincode;
    private String address;
    private String city;
    private String country;

    public UpdateProfileAddress(String address_type, int pincode, String address, String city, String country) {
        this.address_type = address_type;
        this.pincode = pincode;
        this.address = address;
        this.city = city;
        this.country = country;
    }
}
