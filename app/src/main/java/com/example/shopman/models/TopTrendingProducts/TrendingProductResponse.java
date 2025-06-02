package com.example.shopman.models.TopTrendingProducts;

import com.example.shopman.models.TopTrendingProducts.TrendingProduct;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrendingProductResponse {
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
        @SerializedName("products")
        private List<TrendingProduct> products;

        @SerializedName("nextCursor")
        private float nextCursor;

        public List<TrendingProduct> getProducts() { return products; }
        public float getNextCursor() { return nextCursor; }
    }
}