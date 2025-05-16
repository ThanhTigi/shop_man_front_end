package com.example.shopman.models.ProductDetails;

public class ProductDetailMetadata {
    private String message;
    private ProductDetail metadata;

    // Getters and setters

    public ProductDetail getMetadata() {
        return metadata;
    }

    public void setMetadata(ProductDetail metadata) {
        this.metadata = metadata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}