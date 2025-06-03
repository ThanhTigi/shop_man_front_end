package com.example.shopman.models.Campaign;

import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CampaignProductsResponse {
    @SerializedName("message") private String message;
    @SerializedName("status") private int status;
    @SerializedName("metadata") private ProductsMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public ProductsMetadata getMetadata() { return metadata; }

    public static class ProductsMetadata {
        @SerializedName("message") private String message;
        @SerializedName("metadata") private ProductData metadata;

        public String getMessage() { return message; }
        public ProductData getMetadata() { return metadata; }
    }

    public static class ProductData {
        @SerializedName("products") private List<Product> products;
        @SerializedName("limit") private int limit;
        @SerializedName("total") private int total;
        @SerializedName("totalPages") private int totalPages;

        public List<Product> getProducts() { return products; }
        public int getLimit() { return limit; }
        public int getTotal() { return total; }
        public int getTotalPages() { return totalPages; }
    }
}