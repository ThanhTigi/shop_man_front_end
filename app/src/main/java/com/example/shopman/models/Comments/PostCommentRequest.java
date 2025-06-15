package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PostCommentRequest {
    @SerializedName("content")
    private String content;
    @SerializedName("rating")
    private Integer rating;
    @SerializedName("parentId")
    private Integer parentId;
    @SerializedName("image_urls")
    private List<String> imageUrls;

    public PostCommentRequest(String content, Integer rating, Integer parentId, List<String> imageUrls) {
        this.content = content;
        this.rating = rating;
        this.parentId = parentId;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    // Getter, Setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}