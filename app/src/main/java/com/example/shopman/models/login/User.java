package com.example.shopman.models.login;

import com.google.gson.Gson;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String status;
    private String avatar;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
// Getters and Setters

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static User fromJson(String json)
    {
        Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        return user;
    }
}