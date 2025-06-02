package com.example.shopman.models.DealofTheDay;

import com.example.shopman.models.DealofTheDay.DealProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DealProductResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public Metadata getMetadata() { return metadata; }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private MetadataInner metadata;

        public MetadataInner getMetadata() { return metadata; }
    }

    public static class MetadataInner {
        @SerializedName("totalItems")
        private int totalItems;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("currentPage")
        private int currentPage;

        @SerializedName("products")
        private List<DealProduct> products;

        public int getTotalItems() { return totalItems; }
        public int getTotalPages() { return totalPages; }
        public int getCurrentPage() { return currentPage; }
        public List<DealProduct> getProducts() { return products; }
    }
}