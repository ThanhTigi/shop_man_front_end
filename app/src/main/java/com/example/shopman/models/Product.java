package com.example.shopman.models;

import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Product implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private long price;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("rating")
    private float rating;

    @SerializedName("discount_percentage")
    private int discountPercentage;

    @SerializedName("slug")
    private String slug;

    @SerializedName("sale_count")
    private int saleCount;

    @Nullable
    @SerializedName("desc")
    private String desc;

    @Nullable
    @SerializedName("sku_no")
    private String skuNo;

    @Nullable
    @SerializedName("quantity")
    private Integer quantity;

    @Nullable
    @SerializedName("variant")
    private Map<String, Object> variant;

    @Nullable
    @SerializedName("discounts")
    private List<Discount> discounts;

    private boolean isSelected;

    // Constructor chính
    public Product(String id, String name, long price, String thumb, float rating, int discountPercentage,
                   String slug, int saleCount, @Nullable String desc) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.thumb = thumb;
        this.rating = rating;
        this.discountPercentage = discountPercentage;
        this.slug = slug;
        this.saleCount = saleCount;
        this.desc = desc;
        this.isSelected = false;
    }

    // Constructor cho cart
    public Product(String id, String name, long price, String thumb, int quantity, String skuNo,
                   @Nullable Map<String, Object> variant) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.thumb = thumb;
        this.quantity = quantity;
        this.skuNo = skuNo;
        this.variant = variant;
        this.rating = 0f;
        this.discountPercentage = 0;
        this.saleCount = 0;
        this.isSelected = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }
    public String getThumb() { return thumb; }
    public void setThumb(String thumb) { this.thumb = thumb; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public int getSaleCount() { return saleCount; }
    public void setSaleCount(int saleCount) { this.saleCount = saleCount; }
    @Nullable
    public String getDesc() { return desc; }
    public void setDesc(@Nullable String desc) { this.desc = desc; }
    @Nullable
    public String getSkuNo() { return skuNo; }
    public void setSkuNo(@Nullable String skuNo) { this.skuNo = skuNo; }
    @Nullable
    public Integer getQuantity() { return quantity; }
    public void setQuantity(@Nullable Integer quantity) { this.quantity = quantity; }
    @Nullable
    public Map<String, Object> getVariant() { return variant; }
    public void setVariant(@Nullable Map<String, Object> variant) { this.variant = variant; }
    @Nullable
    public List<Discount> getDiscounts() { return discounts; }
    public void setDiscounts(@Nullable List<Discount> discounts) { this.discounts = discounts; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    // Hiển thị variant
    public String getVariantDisplay() {
        if (variant == null || variant.isEmpty()) return "";
        StringBuilder display = new StringBuilder();
        if (variant.containsKey("size")) display.append("Size: ").append(variant.get("size")).append(", ");
        if (variant.containsKey("color")) display.append("Color: ").append(variant.get("color"));
        return display.toString().trim().replaceAll(",$", "");
    }

    // Parcelable implementation
    protected Product(android.os.Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readLong();
        thumb = in.readString();
        rating = in.readFloat();
        discountPercentage = in.readInt();
        slug = in.readString();
        saleCount = in.readInt();
        desc = in.readString();
        skuNo = in.readString();
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        variant = (Map<String, Object>) in.readSerializable();
        discounts = in.createTypedArrayList(Discount.CREATOR);
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeLong(price);
        dest.writeString(thumb);
        dest.writeFloat(rating);
        dest.writeInt(discountPercentage);
        dest.writeString(slug);
        dest.writeInt(saleCount);
        dest.writeString(desc);
        dest.writeString(skuNo);
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
        dest.writeSerializable((java.io.Serializable) variant);
        dest.writeTypedList(discounts);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(android.os.Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}