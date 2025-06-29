package com.example.shopman.models.DealofTheDay;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OuterDealProductMetadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private DealProductMetadata metadata;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public DealProductMetadata getMetadata() { return metadata; }
    public void setMetadata(DealProductMetadata metadata) { this.metadata = metadata; }
}