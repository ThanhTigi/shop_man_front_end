package com.example.shopman.fragments.cart;

import java.io.Serializable;

public class Coupon implements Serializable {
    private String code;
    private String expiryDate;
    private int remainingQuantity;
    private double discountPercentage;

    public Coupon(String code, String expiryDate, int remainingQuantity, double discountPercentage) {
        this.code = code;
        this.expiryDate = expiryDate;
        this.remainingQuantity = remainingQuantity;
        this.discountPercentage = discountPercentage;
    }

    public String getCode() {
        return code;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double calculateDiscount(double orderTotal) {
        double discount = orderTotal * (discountPercentage / 100.0);
        // Đảm bảo không giảm quá tổng hóa đơn
        return Math.min(discount, orderTotal);
    }
}
