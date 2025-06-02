package com.example.shopman.models.wishlist;

public class WishlistMetadata {
    private String message;
    private WishlistData metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public WishlistData getMetadata() { return metadata; }
    public void setMetadata(WishlistData metadata) { this.metadata = metadata; }
}