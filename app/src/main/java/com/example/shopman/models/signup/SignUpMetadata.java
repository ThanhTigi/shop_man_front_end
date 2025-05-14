package com.example.shopman.models.signup;

import com.example.shopman.models.login.InnerMetadata;
import com.google.gson.annotations.SerializedName;

public class SignUpMetadata {
    @SerializedName("metadata")
    private InnerMetadata userMetadata;

    public InnerMetadata getMetadata() {
        return userMetadata;
    }
}