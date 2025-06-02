package com.example.shopman.models.category;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryProductResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private MetadataDetails metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public MetadataDetails getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataDetails metadata) {
            this.metadata = metadata;
        }
    }

    public static class MetadataDetails {
        @SerializedName("data")
        private List<SearchProduct> data;

        @SerializedName("total")
        private int total;

        @SerializedName("suggest")
        private List<String> suggest;

        @SerializedName("lastSortValues")
        private List<Object> lastSortValues;

        public List<SearchProduct> getData() {
            return data;
        }

        public void setData(List<SearchProduct> data) {
            this.data = data;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<String> getSuggest() {
            return suggest;
        }

        public void setSuggest(List<String> suggest) {
            this.suggest = suggest;
        }

        public List<Object> getLastSortValues() {
            return lastSortValues;
        }

        public void setLastSortValues(List<Object> lastSortValues) {
            this.lastSortValues = lastSortValues;
        }
    }
}