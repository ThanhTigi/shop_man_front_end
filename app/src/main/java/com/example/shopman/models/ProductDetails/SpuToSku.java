package com.example.shopman.models.ProductDetails;

public class SpuToSku {
    private int id;
    private String sku_no;
    private String spu_no;
    private int ProductId;
    private Sku Sku;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku_no() {
        return sku_no;
    }

    public void setSku_no(String sku_no) {
        this.sku_no = sku_no;
    }

    public String getSpu_no() {
        return spu_no;
    }

    public void setSpu_no(String spu_no) {
        this.spu_no = spu_no;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
        ProductId = productId;
    }

    public com.example.shopman.models.ProductDetails.Sku getSku() {
        return Sku;
    }

    public void setSku(com.example.shopman.models.ProductDetails.Sku sku) {
        Sku = sku;
    }
// Getters and setters
}





