package com.example.shopman.models.Shop;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ShopProductsResponse {
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
        @SerializedName("data") private List<SearchProduct> data;
        @SerializedName("total") private int total;
        @SerializedName("suggest") private List<String> suggest;
        @SerializedName("lastSortValues") private List<Object> lastSortValues;

        public List<SearchProduct> getData() { return data; }
        public int getTotal() { return total; }
        public List<String> getSuggest() { return suggest; }
        public List<Object> getLastSortValues() { return lastSortValues; }
    }
}