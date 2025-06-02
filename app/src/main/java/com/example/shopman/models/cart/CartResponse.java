package com.example.shopman.models.cart;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private CartMetadata metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public CartMetadata getMetadata() { return metadata; }
    public void setMetadata(CartMetadata metadata) { this.metadata = metadata; }

    public static class CartMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private List<CartItemResponse> metadata;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<CartItemResponse> getMetadata() { return metadata; }
        public void setMetadata(List<CartItemResponse> metadata) { this.metadata = metadata; }
    }
}