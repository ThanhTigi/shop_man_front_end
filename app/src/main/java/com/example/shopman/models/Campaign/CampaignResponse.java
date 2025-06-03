package com.example.shopman.models.Campaign;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CampaignResponse {
    @SerializedName("message") private String message;
    @SerializedName("status") private int status;
    @SerializedName("metadata") private CampaignMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public CampaignMetadata getMetadata() { return metadata; }

    public static class CampaignMetadata {
        @SerializedName("message") private String message;
        @SerializedName("metadata") private CampaignDetail metadata;

        public String getMessage() { return message; }
        public CampaignDetail getMetadata() { return metadata; }
    }

    public static class CampaignDetail {
        @SerializedName("campaign") private Campaign campaign;
        @SerializedName("discount") private List<Discount> discount;

        public Campaign getCampaign() { return campaign; }
        public List<Discount> getDiscount() { return discount; }
    }

    public static class Campaign {
        @SerializedName("id") private int id;
        @SerializedName("title") private String title;
        @SerializedName("slug") private String slug;
        @SerializedName("description") private String description;
        @SerializedName("start_time") private String startTime;
        @SerializedName("end_time") private String endTime;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getSlug() { return slug; }
        public String getDescription() { return description; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
    }

    public static class Discount {
        @SerializedName("id") private int id;
        @SerializedName("name") private String name;
        @SerializedName("code") private String code;
        @SerializedName("value") private String value;
        @SerializedName("type") private String type;
        @SerializedName("StartDate") private String startDate;
        @SerializedName("EndDate") private String endDate;
        @SerializedName("MinValueOrders") private String minValueOrders;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCode() { return code; }
        public String getValue() { return value; }
        public String getType() { return type; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getMinValueOrders() { return minValueOrders; }
    }
}