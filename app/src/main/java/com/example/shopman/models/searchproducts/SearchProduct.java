package com.example.shopman.models.searchproducts;

import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class SearchProduct {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("price")
    private String price;

    @SerializedName("rating")
    private float rating;

    @SerializedName("desc")
    private String desc;

    @SerializedName("ShopId")
    private int shopId;

    @SerializedName("slug")
    private String slug;

    @SerializedName("sale_count")
    private int saleCount;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("CategoryId")
    private int categoryId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("desc_plain")
    private String descPlain;

    @SerializedName("score")
    private float score;

    @SerializedName("attrs")
    private Map<String, Object> attrs;

    @SerializedName("status")
    private String status;

    @SerializedName("CategoryPath")
    private List<Integer> categoryPath;

    @SerializedName("sort")
    private int sort;

    @SerializedName("has_variations")
    private boolean hasVariations;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("discounts")
    private List<Discount> discounts;

    // Ánh xạ sang Product
    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L;
        }
        Product product = new Product(id, name, priceValue, thumb, rating, discountPercentage, slug, saleCount, desc);
        product.setDiscounts(discounts);
        return product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescPlain() {
        return descPlain;
    }

    public void setDescPlain(String descPlain) {
        this.descPlain = descPlain;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Integer> getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(List<Integer> categoryPath) {
        this.categoryPath = categoryPath;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public boolean isHasVariations() {
        return hasVariations;
    }

    public void setHasVariations(boolean hasVariations) {
        this.hasVariations = hasVariations;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }
// Getters and setters (giữ nguyên)
}