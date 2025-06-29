package com.example.shopman.models.Campaign;

import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CampaignProductsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private CampaignProductsMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public CampaignProductsMetadata getMetadata() { return metadata; }

    public static class CampaignProductsMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private CampaignProductsDetail metadata;

        public String getMessage() { return message; }
        public CampaignProductsDetail getMetadata() { return metadata; }
    }

    public static class CampaignProductsDetail {
        @SerializedName("products")
        private List<Product> products;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total")
        private int total;

        @SerializedName("totalPages")
        private int totalPages;

        public List<Product> getProducts() { return products; }
        public int getLimit() { return limit; }
        public int getTotal() { return total; }
        public int getTotalPages() { return totalPages; }
    }
}