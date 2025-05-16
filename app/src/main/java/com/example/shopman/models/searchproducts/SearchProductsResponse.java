package com.example.shopman.models.searchproducts;

public class SearchProductsResponse {
    private String message;
    private int status;
    private SearchProductMetadata metadata;

    // Getters and setters

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

    public SearchProductMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SearchProductMetadata metadata) {
        this.metadata = metadata;
    }
}