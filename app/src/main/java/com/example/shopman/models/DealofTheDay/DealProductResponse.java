package com.example.shopman.models.DealofTheDay;

import com.google.gson.annotations.SerializedName;

public class DealProductResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private OuterDealProductMetadata metadata;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public OuterDealProductMetadata getMetadata() { return metadata; }
    public void setMetadata(OuterDealProductMetadata metadata) { this.metadata = metadata; }
}