package com.example.shopman.models.Comments;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    @SerializedName("id")
    private int id;

    @SerializedName("UserId")
    private int userId;

    @SerializedName("ProductId")
    private int productId;

    @SerializedName("ParentId")
    private Integer parentId;

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("content")
    private String content;

    @SerializedName("image_urls")
    private List<String> imageUrls; // Để null nếu API trả về null

    @SerializedName("left")
    private int left;

    @SerializedName("right")
    private int right;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("user")
    private User user;

    @SerializedName("isEditable")
    private boolean isEditable;

    @SerializedName("isDeletable")
    private boolean isDeletable;

    private List<Comment> replies; // Danh sách reply
    private String nextReplyCursor; // Cursor cho phân trang replies

    // Constructor mặc định, không khởi tạo imageUrls
    public Comment() {
        this.replies = new ArrayList<>();
        Log.d("CommentModel", "Comment initialized, ID: " + id + ", imageUrls: " + (imageUrls != null ? imageUrls.toString() : "null"));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean deletable) {
        this.isDeletable = deletable;
    }

    public List<Comment> getReplies() {
        return replies != null ? replies : new ArrayList<>(); // Trả về danh sách rỗng nếu null
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public String getNextReplyCursor() {
        return nextReplyCursor;
    }

    public void setNextReplyCursor(String nextReplyCursor) {
        this.nextReplyCursor = nextReplyCursor;
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar")
        private String avatar;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}