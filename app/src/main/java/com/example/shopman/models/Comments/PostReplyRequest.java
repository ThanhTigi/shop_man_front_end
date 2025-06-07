package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

public class PostReplyRequest {
    @SerializedName("content")
    String content;
    @SerializedName("ParentId")
    Integer parentId;

    PostReplyRequest(String content, Integer parentId) {
        this.content = content;
        this.parentId = parentId;
    }
}
