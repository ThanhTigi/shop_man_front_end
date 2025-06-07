package com.example.shopman.models.Comments;

import java.util.List;

public class CommentResponse {
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
        private int totalItems;
        private int totalPages;
        private int currentPage;
        private List<Comment> comments;

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public List<Comment> getComments() {
            return comments;
        }

        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }
    }
}