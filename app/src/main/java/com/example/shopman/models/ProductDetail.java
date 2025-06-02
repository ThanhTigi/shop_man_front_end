package com.example.shopman.models;

import com.example.shopman.models.ProductDetails.SpuToSku;
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
    private String descPlain;

    @SerializedName("price")
    private String price; // Lưu dưới dạng String, parse thành long nếu cần

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("attrs")
    private Map<String, Object> attrs; // Sửa thành Map<String, Object>

    @SerializedName("status")
    private String status;

    @SerializedName("slug")
    private String slug;

    @SerializedName("CategoryId")
    private int categoryId;

    @SerializedName("CategoryPath")
    private List<Integer> categoryPath;

    @SerializedName("sort")
    private int sort;

    @SerializedName("ShopId")
    private int shopId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("sale_count")
    private int saleCount;

    @SerializedName("has_variations")
    private boolean hasVariations;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("SpuToSkus")
    private List<SpuToSku> spuToSkus;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getDescPlain() {
        return descPlain;
    }

    public String getPrice() {
        return price;
    }

    public long getPriceAsLong() {
        try {
            return Long.parseLong(price);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public String getThumb() {
        return thumb;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public String getStatus() {
        return status;
    }

    public String getSlug() {
        return slug;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public List<Integer> getCategoryPath() {
        return categoryPath;
    }

    public int getSort() {
        return sort;
    }

    public int getShopId() {
        return shopId;
    }

    public float getRating() {
        return rating;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public boolean isHasVariations() {
        return hasVariations;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public List<SpuToSku> getSpuToSkus() {
        return spuToSkus;
    }
}