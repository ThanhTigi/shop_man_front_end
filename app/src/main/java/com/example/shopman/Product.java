package com.example.shopman;

public class Product {
    private String name;
    private String description;
    private String price;
    private int imageResId;
    private float rating; // New field for rating

    public Product(String name, String description, String price, int imageResId, float rating) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.rating = rating;
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
}