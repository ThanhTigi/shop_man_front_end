package com.example.shopman.models.cart;

public class CartAddResponse {
    private String message;
    private int status;
    private CartMetadata metadata;

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

    public CartMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(CartMetadata metadata) {
        this.metadata = metadata;
    }

    public static class CartMetadata {
        private String message;
        private CartItem metadata;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public CartItem getMetadata() {
            return metadata;
        }

        public void setMetadata(CartItem metadata) {
            this.metadata = metadata;
        }
    }

    public static class CartItem {
        private int productId;
        private String productName;
        private String skuNo;
        private int quantity;
        private String price;
        private String image;
        private Variant variant;

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getSkuNo() {
            return skuNo;
        }

        public void setSkuNo(String skuNo) {
            this.skuNo = skuNo;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Variant getVariant() {
            return variant;
        }

        public void setVariant(Variant variant) {
            this.variant = variant;
        }
    }

    public static class Variant {
        private String size;
        private String color;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}