package com.example.shopman.models.login;


import com.google.gson.annotations.SerializedName;

public class InnerMetadata {
    @SerializedName("user")
    private User user;

    @SerializedName("tokens")
    private Tokens tokens;

    public User getUser() {
        return user;
    }

    public Tokens getTokens() {
        return tokens;
    }
}