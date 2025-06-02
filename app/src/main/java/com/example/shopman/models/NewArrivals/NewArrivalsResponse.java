package com.example.shopman.models.NewArrivals;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewArrivalsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private MetadataDetails metadata;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public MetadataDetails getMetadata() { return metadata; }
        public void setMetadata(MetadataDetails metadata) { this.metadata = metadata; }
    }


}