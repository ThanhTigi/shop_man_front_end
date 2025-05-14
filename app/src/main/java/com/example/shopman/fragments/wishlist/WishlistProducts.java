package com.example.shopman.fragments.wishlist;

import com.example.shopman.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class WishlistProducts {
    private List<Product> totalProducts;

    public List<Product> getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(List<Product> totalProducts) {
        this.totalProducts = totalProducts;
    }

    public WishlistProducts(List<Product> totalProducts) {
        this.totalProducts = totalProducts;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static WishlistProducts fromJson(String json)
    {
        Gson gson = new Gson();
        WishlistProducts wishlistProducts = gson.fromJson(json, WishlistProducts.class);
        return wishlistProducts;
    }

    public void AddProduct(Product product)
    {
        totalProducts.add(product);
    }

    public void RemoveProduct(Product productRemove)
    {
        for (int i = 0; i < totalProducts.size(); i++) {
            Product product = totalProducts.get(i);
            if (product.getName().equals(productRemove.getName())) {
                totalProducts.remove(i);
                break;
            }
        }

    }
}
