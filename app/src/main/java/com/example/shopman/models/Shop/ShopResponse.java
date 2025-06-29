package com.example.shopman.models.Shop;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ShopMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public ShopMetadata getMetadata() { return metadata; }

    public static class ShopMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ShopDetail metadata;

        public String getMessage() { return message; }
        public ShopDetail getMetadata() { return metadata; }
    }

    public static class ShopDetail {
        @SerializedName("shop")
        private Shop shop;

        @SerializedName("discount")
        private List<com.example.shopman.models.Discount> discounts;

        @SerializedName("isFollowing")
        private boolean isFollowing;

        public Shop getShop() { return shop; }
        public List<com.example.shopman.models.Discount> getDiscounts() { return discounts; }
        public boolean getIsFollowing() { return isFollowing; }
        public void setIsFollowing(boolean isFollowing) { this.isFollowing = isFollowing; }
    }

    public static class Shop {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("desc")
        private String desc;

        @SerializedName("slug")
        private String slug;

        @SerializedName("logo")
        private String logo;

        @SerializedName("thumb")
        private String thumb;

        @SerializedName("shopLocation")
        private String shopLocation;

        @SerializedName("rating")
        private String rating;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDesc() { return desc; }
        public String getSlug() { return slug; }
        public String getLogo() { return logo; }
        public String getThumb() { return thumb; }
        public String getShopLocation() { return shopLocation; }
        public String getRating() { return rating; }

        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setDesc(String desc) { this.desc = desc; }
        public void setSlug(String slug) { this.slug = slug; }
        public void setLogo(String logo) { this.logo = logo; }
        public void setThumb(String thumb) { this.thumb = thumb; }
        public void setShopLocation(String shopLocation) { this.shopLocation = shopLocation; }
        public void setRating(String rating) { this.rating = rating; }
    }
}