package com.example.shopman.models.Shop;

import com.google.gson.annotations.SerializedName;

public class ShopInfoResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ShopInfoMetadata metadata;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ShopInfoMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ShopInfoMetadata metadata) {
        this.metadata = metadata;
    }

    public static class ShopInfoMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ShopMetadata metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ShopMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(ShopMetadata metadata) {
            this.metadata = metadata;
        }
    }

    public static class ShopMetadata {
        @SerializedName("shop")
        private Shop shop;

        @SerializedName("isFollowing")
        private boolean isFollowing;

        public Shop getShop() {
            return shop;
        }

        public void setShop(Shop shop) {
            this.shop = shop;
        }

        public boolean isFollowing() {
            return isFollowing;
        }

        public void setFollowing(boolean following) {
            isFollowing = following;
        }
    }

    public static class Shop {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("slug")
        private String slug;

        @SerializedName("status")
        private String status;

        @SerializedName("logo")
        private String logo;

        @SerializedName("desc")
        private String desc;

        @SerializedName("rating")
        private String rating;

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

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }
}