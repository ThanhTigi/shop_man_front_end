package com.example.shopman.models.wishlist;

public class WishlistResponse {
    private String message;
    private int status;
    private WishlistMetadata metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public WishlistMetadata getMetadata() { return metadata; }
    public void setMetadata(WishlistMetadata metadata) { this.metadata = metadata; }
}