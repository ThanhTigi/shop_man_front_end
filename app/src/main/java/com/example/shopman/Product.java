package com.example.shopman;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private String name;
    private String description;
    private String price;
    private int imageResId;
    private float rating;
    private List<String> sizes;
    private String detailedDescription;

    public Product(String name, String description, String price, int imageResId, float rating, List<String> sizes, String detailedDescription) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.rating = rating;
        this.sizes = sizes;
        this.detailedDescription = detailedDescription;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public float getRating() {
        return rating;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }
}