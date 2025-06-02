package com.example.shopman.models.DealofTheDay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MetadataDetails {
    @SerializedName("totalItems")
    private int totalItems;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("products")
    private List<DealProduct> products;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<DealProduct> getProducts() {
        return products;
    }

    public void setProducts(List<DealProduct> products) {
        this.products = products;
    }
}