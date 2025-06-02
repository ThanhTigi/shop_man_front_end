package com.example.shopman.models.TopTrendingProducts;

import com.example.shopman.models.Product;
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

    // Chuyển thành Product để dùng với ProductAdapter
    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L;
        }
        return new Product(
                String.valueOf(id),
                name,
                priceValue,
                thumb,
                rating,
                discountPercentage,
                slug,
                saleCount,
                null // desc không có trong API
        );
    }
}