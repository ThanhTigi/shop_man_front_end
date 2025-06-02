package com.example.shopman.models.ProductDetails;

public class Sku {
    private int id;
    private int ProductId;
    private String sku_no;
    private String sku_name;
    private String sku_desc;
    private int sku_type;
    private String status;
    private int sort;
    private int sku_stock;
    private String sku_price; // Chuỗi vì API trả về chuỗi
    private SkuAttr SkuAttr;
    private SkuSpecs SkuSpecs;

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
        ProductId = productId;
    }

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

    public String getSku_name() {
        return sku_name;
    }

    public void setSku_name(String sku_name) {
        this.sku_name = sku_name;
    }

    public String getSku_desc() {
        return sku_desc;
    }

    public void setSku_desc(String sku_desc) {
        this.sku_desc = sku_desc;
    }

    public int getSku_type() {
        return sku_type;
    }

    public void setSku_type(int sku_type) {
        this.sku_type = sku_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSku_stock() {
        return sku_stock;
    }

    public void setSku_stock(int sku_stock) {
        this.sku_stock = sku_stock;
    }

    public String getSku_price() {
        return sku_price;
    }

    public void setSku_price(String sku_price) {
        this.sku_price = sku_price;
    }

    public com.example.shopman.models.ProductDetails.SkuAttr getSkuAttr() {
        return SkuAttr;
    }

    public void setSkuAttr(com.example.shopman.models.ProductDetails.SkuAttr skuAttr) {
        SkuAttr = skuAttr;
    }

    public com.example.shopman.models.ProductDetails.SkuSpecs getSkuSpecs() {
        return SkuSpecs;
    }

    public void setSkuSpecs(com.example.shopman.models.ProductDetails.SkuSpecs skuSpecs) {
        SkuSpecs = skuSpecs;
    }
// Getters and setters

    public static class Category {
        private String name;
        private String thumbUrl;

        public Category(String name, String thumbUrl) {
            this.name = name;
            this.thumbUrl = thumbUrl;
        }

        public String getName() {
            return name;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }
    }
}