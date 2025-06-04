package com.example.shopman.models.wishlist.Remove;

import com.google.gson.annotations.SerializedName;

public class WishlistRemoveMetadata {
        @SerializedName("message")
        private String message;

        @SerializedName("metadata")
        private int metadata; // Giá trị 1 trong JSON

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getMetadata() {
            return metadata;
        }

        public void setMetadata(int metadata) {
            this.metadata = metadata;
        }
}
