package com.example.shopman.models.ProductDetails;

import com.example.shopman.models.SpuToSku;

import java.util.List;
import java.util.Map;

public class ProductDetail {
    private int id;
    private String name;
    private String desc;
    private String desc_plain;
    private String price; // Chuỗi vì API trả về chuỗi
    private int discount_percentage;
    private String thumb;
    private Map<String, Object> attrs; // Hoặc tạo class Attrs riêng
    private String status;
    private String slug;
    private int CategoryId;
    private List<Integer> CategoryPath;
    private int sort;
    private int ShopId;
    private float rating;
    private int sale_count;
    private boolean has_variations;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private List<SpuToSku> SpuToSkus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc_plain() {
        return desc_plain;
    }

    public void setDesc_plain(String desc_plain) {
        this.desc_plain = desc_plain;
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
// Getters and setters
}