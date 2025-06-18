package com.example.shopman.models.signup;


import com.example.shopman.models.signup.Tokens;
import com.example.shopman.models.signup.User;
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