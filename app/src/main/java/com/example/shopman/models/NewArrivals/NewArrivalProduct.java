package com.example.shopman.models.NewArrivals;

import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

public class NewArrivalProduct {
    @SerializedName("id")
    private int id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("rating")
    private float rating;

    @SerializedName("sale_count")
    private int saleCount;

    // Convert to Product
    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L;
        }
        return new Product(
                String.valueOf(id), // Chuyển int ID thành String
                name,
                priceValue,
                thumb,
                rating,
                discountPercentage,
                slug,
                saleCount,
                null // desc không có trong JSON
        );
    }

    // Getters và setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }
    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public int getSaleCount() { return saleCount; }
    public void setSaleCount(int saleCount) { this.saleCount = saleCount; }
}