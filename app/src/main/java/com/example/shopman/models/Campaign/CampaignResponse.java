package com.example.shopman.models.Campaign;

import com.google.gson.annotations.SerializedName;
import com.example.shopman.models.Discount;

import java.util.List;

public class CampaignResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private CampaignMetadata metadata;

    public String getMessage() { return message; }
    public int getStatus() { return status; }
    public CampaignMetadata getMetadata() { return metadata; }

    public static class CampaignMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private CampaignDetail metadata;

        public String getMessage() { return message; }
        public CampaignDetail getMetadata() { return metadata; }
    }

    public static class CampaignDetail {
        @SerializedName("campaign")
        private Campaign campaign;

        @SerializedName("discount")
        private List<Discount> discounts;

        public Campaign getCampaign() { return campaign; }
        public List<Discount> getDiscounts() { return discounts; }
    }

    public static class Campaign {
        @SerializedName("id")
        private int id;

        @SerializedName("title")
        private String title;

        @SerializedName("slug")
        private String slug;

        @SerializedName("description")
        private String description;

        @SerializedName("thumb")
        private String thumb;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getSlug() { return slug; }
        public String getDescription() { return description; }
        public String getThumb() { return thumb; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
    }
}