package com.example.shopman.models.Banner;

import com.google.gson.annotations.SerializedName;

public class Banner {
    @SerializedName("id")
    private int id;

    @SerializedName("banner_type")
    private String bannerType;

    @SerializedName("title")
    private String title;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("link_type")
    private String linkType; // "campaign", "shop"

    @SerializedName("link_target")
    private String linkTarget;

    @SerializedName("action")
    private String action;

    @SerializedName("position")
    private int position;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("priority")
    private int priority;

    @SerializedName("status")
    private String status;

    @SerializedName("fee")
    private String fee;

    @SerializedName("ShopId")
    private Integer shopId;

    @SerializedName("PartnerId")
    private Integer partnerId;

    @SerializedName("CampaignId")
    private Integer campaignId;

    @SerializedName("slug")
    private String slug;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("DiscountId")
    private Integer discountId;

    // Getters
    public int getId() {
        return id;
    }

    public String getBannerType() {
        return bannerType;
    }

    public String getTitle() {
        return title;
    }

    public String getThumb() {
        return thumb;
    }

    public String getLinkType() {
        return linkType;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public String getAction() {
        return action;
    }

    public int getPosition() {
        return position;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getFee() {
        return fee;
    }

    public Integer getShopId() {
        return shopId;
    }

    public Integer getPartnerId() {
        return partnerId;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public String getSlug() {
        return slug;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public Integer getDiscountId() {
        return discountId;
    }
}