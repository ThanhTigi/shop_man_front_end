package com.example.shopman.models.category;

import com.google.gson.annotations.SerializedName;

public class ApiCategory {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("thumb")
    private String thumb;

    @SerializedName("desc")
    private String desc;

    @SerializedName("status")
    private String status;

    @SerializedName("slug")
    private String slug;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("deletedAt")
    private String deletedAt;

    @SerializedName("ParentId")
    private String parentId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumb() {
        return thumb;
    }

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
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

    public String getDeletedAt() {
        return deletedAt;
    }

    public String getParentId() {
        return parentId;
    }
}