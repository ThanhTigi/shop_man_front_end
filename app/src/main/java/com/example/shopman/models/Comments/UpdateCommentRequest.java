package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

public class UpdateCommentRequest {
    @SerializedName("content")
    String content;

    UpdateCommentRequest(String content) {
        this.content = content;
    }
}