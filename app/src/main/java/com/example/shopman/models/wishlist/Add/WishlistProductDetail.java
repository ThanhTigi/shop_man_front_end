package com.example.shopman.models.wishlist.Add;

import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;

public class WishlistProductDetail extends SearchProduct {
    @SerializedName("wishlistId")
    private int wishlistId; // Trường đặc biệt cho wishlist

    @SerializedName("userId")
    private int userId;     // Trường đặc biệt cho wishlist

    // Getters và setters cho các trường đặc biệt
    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}