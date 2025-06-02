package com.example.shopman.models.wishlist;

import com.example.shopman.models.searchproducts.SearchProduct;

public class WishlistItem {
    private int id;
    private int userId;
    private int productId;
    private String createdAt;
    private String updatedAt;
    private SearchProduct product; // Đảm bảo kiểu là SearchProduct

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public SearchProduct getProduct() { return product; }
    public void setProduct(SearchProduct product) { this.product = product; }
}