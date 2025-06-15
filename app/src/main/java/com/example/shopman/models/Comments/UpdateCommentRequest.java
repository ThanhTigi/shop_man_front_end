package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UpdateCommentRequest {
    @SerializedName("content")
    private String content;
    @SerializedName("rating")
    private Integer rating;
    @SerializedName("image_urls")
    private List<String> imageUrls;

    public UpdateCommentRequest(String content, Integer rating, List<String> imageUrls) {
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    // Getter, Setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}