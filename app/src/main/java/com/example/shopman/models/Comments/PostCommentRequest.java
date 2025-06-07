package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

public class PostCommentRequest {
    @SerializedName("content")
    String content;
    @SerializedName("rating")
    Integer rating;

    PostCommentRequest(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}
