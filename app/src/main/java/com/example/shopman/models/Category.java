package com.example.shopman.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("name")
    private String name;

    @SerializedName("thumb")
    private String thumbUrl;

    @SerializedName("slug")
    private String slug;

    public Category(String name, String thumbUrl, String slug) {
        this.name = name;
        this.thumbUrl = thumbUrl;
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getSlug() {
        return slug;
    }
}