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
        private List<Comment> metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Comment> getMetadata() {
            return metadata;
        }

        public void setMetadata(List<Comment> metadata) {
            this.metadata = metadata;
        }
    }
}
