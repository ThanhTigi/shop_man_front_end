package com.example.shopman.models.NewArrivals;

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
    private List<NewArrivalProduct> products;

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public List<NewArrivalProduct> getProducts() { return products; }
    public void setProducts(List<NewArrivalProduct> products) { this.products = products; }
}
