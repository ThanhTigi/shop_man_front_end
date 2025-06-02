package com.example.shopman.models.wishlist;

import java.util.List;

public class WishlistData {
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private List<WishlistItem> products;

    // Getters and setters
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

    public List<WishlistItem> getProducts() {
        return products;
    }

    public void setProducts(List<WishlistItem> products) {
        this.products = products;
    }
}