package com.example.shopman.fragments.cart;

import com.google.gson.Gson;

import java.util.List;

public class CartProducts {
    private List<CartItem> products;

    public List<CartItem> getProducts() {
        return products;
    }


    public CartProducts(List<CartItem> products) {
        this.products = products;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static CartProducts fromJson(String json)
    {
        Gson gson = new Gson();
        CartProducts cartProducts = gson.fromJson(json, CartProducts.class);
        return cartProducts;
    }
}
