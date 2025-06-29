package com.example.shopman.models.Shop;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopProductsResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ShopProductsMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public ShopProductsMetadata getMetadata() { return metadata; }

    public static class ShopProductsMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ShopProductsDetail metadata;

        public String getMessage() { return message; }
        public ShopProductsDetail getMetadata() { return metadata; }
    }

    public static class ShopProductsDetail {
        @SerializedName("data")
        private List<SearchProduct> data;

        @SerializedName("lastSortValues")
        private List<Object> lastSortValues;

        @SerializedName("total")
        private int total;

        public List<SearchProduct> getData() { return data; }
        public List<Object> getLastSortValues() { return lastSortValues; }
        public int getTotal() { return total; }
    }
}