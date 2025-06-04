package com.example.shopman.models.wishlist.Remove;

import com.google.gson.annotations.SerializedName;

public class WishlistRemoveResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private WishlistRemoveMetadata metadata;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public WishlistRemoveMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(WishlistRemoveMetadata metadata) {
        this.metadata = metadata;
    }
}
