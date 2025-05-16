package com.example.shopman.models;

import java.util.Map;



public class SkuAttr {
    private int id;
    private String sku_no;
    private int sku_stock;
    private String sku_price;
    private Map<String, String> sku_attrs; // size, color

    public String getSku_no() {
        return sku_no;
    }

    public void setSku_no(String sku_no) {
        this.sku_no = sku_no;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Map<String, String> getSku_attrs() {
        return sku_attrs;
    }

    public void setSku_attrs(Map<String, String> sku_attrs) {
        this.sku_attrs = sku_attrs;
    }
// Getters and setters
}