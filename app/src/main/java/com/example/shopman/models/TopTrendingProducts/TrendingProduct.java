package com.example.shopman.models.TopTrendingProducts;

import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.google.gson.annotations.SerializedName;

public class TrendingProduct {
    @SerializedName("id")
    private int id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("name")
    private String name;

    @SerializedName("sale_count")
    private int saleCount;

    @SerializedName("price")
    private String price;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    public void setId(int id) {
        this.id = id;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("rating")
    private float rating;

    // Getters
    public int getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public int getSaleCount() { return saleCount; }
    public String getPrice() { return price; }
    public int getDiscountPercentage() { return discountPercentage; }
    public String getThumb() { return thumb; }
    public float getRating() { return rating; }

    // Chuyển thành Product để dùng với DealProductAdapter
    public SearchProduct toProduct() {
        SearchProduct product = new SearchProduct();
        product.setId(String.valueOf(id));
        product.setName(name);
        product.setPrice(price); // Giữ nguyên String
        product.setSlug(slug);
        product.setSaleCount(saleCount);
        product.setRating(rating);
        product.setThumb(thumb);
        product.setDiscountPercentage(discountPercentage);
        product.setDesc(null); // desc không có trong API
        return product;
    }
}