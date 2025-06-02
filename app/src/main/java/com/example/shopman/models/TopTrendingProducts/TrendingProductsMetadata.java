package com.example.shopman.models.TopTrendingProducts;

import com.google.gson.annotations.SerializedName;

public class TrendingProductsMetadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private TrendingProductsData metadata;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public TrendingProductsData getMetadata() { return metadata; }
    public void setMetadata(TrendingProductsData metadata) { this.metadata = metadata; }
}