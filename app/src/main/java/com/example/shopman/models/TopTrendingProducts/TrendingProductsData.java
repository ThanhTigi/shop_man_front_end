package com.example.shopman.models.TopTrendingProducts;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class TrendingProductsData {
    @SerializedName("products")
    private List<TrendingProduct> products;

    @SerializedName("nextCursor")
    private double nextCursor;

    // Getters and setters
    public List<TrendingProduct> getProducts() { return products; }
    public void setProducts(List<TrendingProduct> products) { this.products = products; }
    public double getNextCursor() { return nextCursor; }
    public void setNextCursor(double nextCursor) { this.nextCursor = nextCursor; }
}