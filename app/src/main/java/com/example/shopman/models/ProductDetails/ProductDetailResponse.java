package com.example.shopman.models.ProductDetails;

public class ProductDetailResponse {
    private String message;
    private int status;
    private ProductDetailMetadata metadata;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProductDetailMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ProductDetailMetadata metadata) {
        this.metadata = metadata;
    }

    // Getters and setters
}