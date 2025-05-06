package com.example.shopman.models;

import com.google.gson.Gson;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String status;
    private String avatar;

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