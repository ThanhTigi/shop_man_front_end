package com.example.shopman.models.searchproducts;

public class SearchProductMetadata {
    private String message;
    private SearchProductsData metadata;

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SearchProductsData getMetadata() {
        return metadata;
    }

    public void setMetadata(SearchProductsData metadata) {
        this.metadata = metadata;
    }
}