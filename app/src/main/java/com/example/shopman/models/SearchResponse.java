package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private SearchMetadata searchMetadata;

        public String getMessage() {
            return message;
        }

        public SearchMetadata getSearchMetadata() {
            return searchMetadata;
        }
    }

    public static class SearchMetadata {
        @SerializedName("data")
        private List<Product> data;

        @SerializedName("total")
        private int total;

        @SerializedName("suggest")
        private List<String> suggest;

        @SerializedName("lastSortValues")
        private List<Object> lastSortValues; // [score, id]

        public List<Product> getData() {
            return data;
        }

        public int getTotal() {
            return total;
        }

        public List<String> getSuggest() {
            return suggest;
        }

        public List<Object> getLastSortValues() {
            return lastSortValues;
        }
    }
}