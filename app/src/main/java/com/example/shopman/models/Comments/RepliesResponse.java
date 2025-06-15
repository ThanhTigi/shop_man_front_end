package com.example.shopman.models.Comments;

import java.util.List;

public class RepliesResponse {
    private String message;
    private int status;
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
        private String message;
        private RepliesMetadata metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public RepliesMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(RepliesMetadata metadata) {
            this.metadata = metadata;
        }
    }

    public static class RepliesMetadata {
        private int totalItems;
        private List<Comment> comments;
        private String nextCursor;

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public List<Comment> getComments() {
            return comments;
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