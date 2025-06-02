package com.example.shopman.models.DealofTheDay;

import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class DealProduct {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("desc_plain")
    private String descPlain;

    @SerializedName("price")
    private String price;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("attrs")
    private Map<String, Object> attrs;

    @SerializedName("status")
    private String status;

    @SerializedName("slug")
    private String slug;

    @SerializedName("CategoryId")
    private int categoryId;

    @SerializedName("ShopId")
    private int shopId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("sale_count")
    private int saleCount;

    @SerializedName("has_variations")
    private boolean hasVariations;

    @SerializedName("discounts")
    private List<Discount> discounts;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDesc() { return desc; }
    public String getDescPlain() { return descPlain; }
    public String getPrice() { return price; }
    public int getDiscountPercentage() { return discountPercentage; }
    public String getThumb() { return thumb; }
    public Map<String, Object> getAttrs() { return attrs; }
    public String getStatus() { return status; }
    public String getSlug() { return slug; }
    public int getCategoryId() { return categoryId; }
    public int getShopId() { return shopId; }
    public float getRating() { return rating; }
    public int getSaleCount() { return saleCount; }
    public boolean isHasVariations() { return hasVariations; }
    public List<Discount> getDiscounts() { return discounts; }

    // Chuyển thành Product để dùng với ProductAdapter
    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L;
        }
        Product product = new Product(
                String.valueOf(id),
                name,
                priceValue,
                thumb,
                rating,
                discountPercentage,
                slug,
                saleCount,
                desc
        );
        product.setDiscounts(discounts);
        return product;
    }
}