package com.example.shopman.models.searchproducts;

import java.util.List;

public class SearchProduct {
    private int CategoryId;
    private String createdAt;
    private int sale_count;
    private String desc_plain;
    private String thumb;
    private String price;
    private float rating;
    private String name;
    private int discount_percentage;

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    private String slug;
    private int ShopId;
    private String desc;
    private String id;
    private float score;
    private List<Object> sortValues; // Có thể dùng List<String> nếu server trả về cố định [score, id]

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getSale_count() {
        return sale_count;
    }

    public void setSale_count(int sale_count) {
        this.sale_count = sale_count;
    }

    public String getDesc_plain() {
        return desc_plain;
    }

    public void setDesc_plain(String desc_plain) {
        this.desc_plain = desc_plain;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(int discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<Object> getSortValues() {
        return sortValues;
    }

    public void setSortValues(List<Object> sortValues) {
        this.sortValues = sortValues;
    }

    // Getters and setters
}
