package com.example.shopman.models.ProductDetails;

import com.example.shopman.models.ProductDetail;
import com.google.gson.annotations.SerializedName;

public class ProductDetailResponse {
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
        private ProductDetail productDetail;

        public String getMessage() {
            return message;
        }

        public ProductDetail getProductDetail() {
            return productDetail;
        }
    }
}