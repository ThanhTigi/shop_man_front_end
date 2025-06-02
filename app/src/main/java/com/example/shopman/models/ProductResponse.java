package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ProductMetadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ProductMetadata getMetadata() {
        return metadata;
    }

    public static class ProductMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ProductData metadata;

        public String getMessage() {
            return message;
        }

        public ProductData getMetadata() {
            return metadata;
        }
    }

    public static class ProductData {
        @SerializedName("products")
        private List<Product> products;

        @SerializedName("data") // Dành cho API của shop
        private List<Product> data;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total")
        private int total;

        @SerializedName("totalPages")
        private int totalPages;

        public List<Product> getProducts() {
            return products != null ? products : data; // Hỗ trợ cả products (campaign) và data (shop)
        }

        public int getLimit() {
            return limit;
        }

        public int getTotal() {
            return total;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}