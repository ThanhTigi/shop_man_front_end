package com.example.shopman.models.category;

import com.example.shopman.models.Category;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryMetadata {
    @SerializedName("message")
    private String message;

    @SerializedName("metadata")
    private List<Category> metadata;

    public String getMessage() {
        return message;
    }

    public List<Category> getMetadata() {
        return metadata;
    }
}