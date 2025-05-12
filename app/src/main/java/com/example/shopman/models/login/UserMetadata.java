package com.example.shopman.models.login;

import com.google.gson.Gson;

public class UserMetadata {
    private User user;
    private Tokens tokens;

    public User getUser() {
        return user;
    }

    public Tokens getTokens() {
        return tokens;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static UserMetadata fromJson(String json)
    {
        Gson gson = new Gson();
        UserMetadata user = gson.fromJson(json, UserMetadata.class);
        return user;
    }
}
