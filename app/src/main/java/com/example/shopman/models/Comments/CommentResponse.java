package com.example.shopman.models.Comments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CommentResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    @SerializedName("metadata")
    private Metadata metadata;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public static class Metadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private CommentMetadata metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public CommentMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(CommentMetadata metadata) {
            this.metadata = metadata;
        }
    }

    public static class CommentMetadata {
        @SerializedName("totalItems")
        private int totalItems;

        @SerializedName("comments")
        private List<Comment> comments;

        @SerializedName("nextCursor")
        private String nextCursor;

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public List<Comment> getComments() {
            return comments != null ? comments : new ArrayList<>(); // Trả về danh sách rỗng nếu null
        }

        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }

        public String getNextCursor() {
            return nextCursor;
        }

        public void setNextCursor(String nextCursor) {
            this.nextCursor = nextCursor;
        }
    }
}