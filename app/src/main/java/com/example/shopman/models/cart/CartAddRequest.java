package com.example.shopman.models.cart;

public class CartAddRequest {
    private String ProductId;
    private String skuNo;
    private int quantity;

    public CartAddRequest(String ProductId, String skuNo, int quantity) {
        this.ProductId = ProductId;
        this.skuNo = skuNo;
        this.quantity = quantity;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String ProductId) {
        this.ProductId = ProductId;
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
}