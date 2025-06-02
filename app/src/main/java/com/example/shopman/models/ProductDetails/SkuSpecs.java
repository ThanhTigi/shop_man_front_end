package com.example.shopman.models.ProductDetails;

import java.util.Map;


public class SkuSpecs {
    private int id;
    private Map<String, String> sku_specs; // fit, weight, material
    private int SkuId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkuId() {
        return SkuId;
    }

    public void setSkuId(int skuId) {
        SkuId = skuId;
    }

    public Map<String, String> getSku_specs() {
        return sku_specs;
    }

    public void setSku_specs(Map<String, String> sku_specs) {
        this.sku_specs = sku_specs;
    }
// Getters and setters
}