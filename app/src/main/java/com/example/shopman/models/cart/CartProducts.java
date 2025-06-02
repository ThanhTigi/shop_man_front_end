package com.example.shopman.models.cart;

import com.example.shopman.models.Product;
import com.google.gson.Gson;

import java.util.List;

public class CartProducts {
    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public CartProducts(List<Product> products) {
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static CartProducts fromJson(String json) {
        Gson gson = new Gson();
        CartProducts cartProducts = gson.fromJson(json, CartProducts.class);
        return cartProducts;
    }
}