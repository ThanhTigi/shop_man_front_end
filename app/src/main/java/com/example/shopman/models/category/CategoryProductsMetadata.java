package com.example.shopman.models.category;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryProductsMetadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private ProductsData metadata;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public ProductsData getMetadata() { return metadata; }
    public void setMetadata(ProductsData metadata) { this.metadata = metadata; }

    public static class ProductsData {
        @SerializedName("data")
        private List<SearchProduct> data; // Thay Product bằng SearchProduct

        @SerializedName("total")
        private int total;

        @SerializedName("suggest")
        private List<String> suggest;

        @SerializedName("lastSortValues")
        private List<Object> lastSortValues;

        public List<SearchProduct> getData() { return data; }
        public void setData(List<SearchProduct> data) { this.data = data; }
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public List<String> getSuggest() { return suggest; }
        public void setSuggest(List<String> suggest) { this.suggest = suggest; }
        public List<Object> getLastSortValues() { return lastSortValues; }
        public void setLastSortValues(List<Object> lastSortValues) { this.lastSortValues = lastSortValues; }
    }
}