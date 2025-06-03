package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private ShopMetadata metadata;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public ShopMetadata getMetadata() {
        return metadata;
    }

    public static class ShopMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private ShopDetail metadata;

        public String getMessage() {
            return message;
        }

        public ShopDetail getMetadata() {
            return metadata;
        }
    }

    public static class ShopDetail {
        @SerializedName("shop")
        private Shop shop;

        @SerializedName("discount")
        private List<Discount> discounts;

        public Shop getShop() {
            return shop;
        }

        public List<Discount> getDiscounts() {
            return discounts;
        }
    }

    public static class Shop {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("desc")
        private String description;

        @SerializedName("slug")
        private String slug;

        @SerializedName("logo")
        private String logo;

        @SerializedName("shopLocation")
        private String shopLocation;

        @SerializedName("rating")
        private String rating;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public void setShopLocation(String shopLocation) {
            this.shopLocation = shopLocation;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public String getSlug() {
            return slug;
        }

        public String getLogo() {
            return logo;
        }

        public String getShopLocation() {
            return shopLocation;
        }

        public String getRating() {
            return rating;
        }
    }

    public static class Discount {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("desc")
        private String description;

        @SerializedName("value")
        private String value;

        @SerializedName("type")
        private String type;

        @SerializedName("code")
        private String code;

        @SerializedName("StartDate")
        private String startDate;

        @SerializedName("EndDate")
        private String endDate;

        @SerializedName("MaxUses")
        private int maxUses;

        @SerializedName("UserCounts")
        private int userCounts;

        @SerializedName("MinValueOrders")
        private String minValueOrders;

        @SerializedName("status")
        private String status;

        @SerializedName("ShopId")
        private Integer shopId;

        @SerializedName("CampaignId")
        private Integer campaignId;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("updatedAt")
        private String updatedAt;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public int getMaxUses() {
            return maxUses;
        }

        public int getUserCounts() {
            return userCounts;
        }

        public String getMinValueOrders() {
            return minValueOrders;
        }

        public String getStatus() {
            return status;
        }

        public Integer getShopId() {
            return shopId;
        }

        public Integer getCampaignId() {
            return campaignId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}