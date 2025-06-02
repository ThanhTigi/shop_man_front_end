package com.example.shopman.models.ProductDetails;

import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ProductDetail {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("desc_plain")
    private String desc_plain;

    @SerializedName("price")
    private String price;

    @SerializedName("discount_percentage")
    private int discount_percentage;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("attrs")
    private Map<String, Object> attrs;

    @SerializedName("status")
    private String status;

    @SerializedName("slug")
    private String slug;

    @SerializedName("CategoryId")
    private int CategoryId;

    @SerializedName("CategoryPath")
    private List<Integer> CategoryPath;

    @SerializedName("sort")
    private int sort;

    @SerializedName("ShopId")
    private int ShopId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("sale_count")
    private int sale_count;

    @SerializedName("has_variations")
    private boolean has_variations;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("SpuToSkus")
    private List<SpuToSku> SpuToSkus;

    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L;
        }
        return new Product(String.valueOf(id), name, priceValue, thumb, rating, discount_percentage, slug, sale_count, desc);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc_plain() {
        return desc_plain;
    }

    public void setDesc_plain(String desc_plain) {
        this.desc_plain = desc_plain;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(int discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public List<Integer> getCategoryPath() {
        return CategoryPath;
    }

    public void setCategoryPath(List<Integer> categoryPath) {
        CategoryPath = categoryPath;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setShopId(int shopId) {
        ShopId = shopId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getSale_count() {
        return sale_count;
    }

    public void setSale_count(int sale_count) {
        this.sale_count = sale_count;
    }

    public boolean isHas_variations() {
        return has_variations;
    }

    public void setHas_variations(boolean has_variations) {
        this.has_variations = has_variations;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public List<SpuToSku> getSpuToSkus() {
        return SpuToSkus;
    }

    public void setSpuToSkus(List<SpuToSku> spuToSkus) {
        SpuToSkus = spuToSkus;
    }
// Getters and setters (giữ nguyên)
}