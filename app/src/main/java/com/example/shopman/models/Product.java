package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Product {
    @SerializedName("CategoryId")
    private int categoryId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("sale_count")
    private int saleCount;

    @SerializedName("desc_plain")
    private String descPlain;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("price")
    private long price;

    @SerializedName("rating")
    private float rating;

    @SerializedName("name")
    private String name;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("slug")
    private String slug;

    @SerializedName("ShopId")
    private int shopId;

    @SerializedName("desc")
    private String desc;

    @SerializedName("id")
    private String id;

    @SerializedName("score")
    private float score;

    @SerializedName("sortValues")
    private List<Object> sortValues; // [score, id]

    // Getters
    public int getCategoryId() {
        return categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public String getDescPlain() {
        return descPlain;
    }

    public String getThumb() {
        return thumb;
    }

    public long getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public String getSlug() {
        return slug;
    }

    public int getShopId() {
        return shopId;
    }

    public String getDesc() {
        return desc;
    }

    public String getId() {
        return id;
    }

    public float getScore() {
        return score;
    }

    public List<Object> getSortValues() {
        return sortValues;
    }
}