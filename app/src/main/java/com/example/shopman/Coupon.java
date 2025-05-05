package com.example.shopman;

import java.io.Serializable;

public class Coupon implements Serializable {
    private String code; // Mã giảm giá
    private String expiryDate; // Ngày hết hạn
    private int remainingQuantity; // Số lượng còn lại
    private double discountPercentage; // Phần trăm giảm giá (0% - 80%)

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

    // Tính số tiền giảm dựa trên tổng hóa đơn
    public double calculateDiscount(double orderTotal) {
        double discount = orderTotal * (discountPercentage / 100.0);
        // Đảm bảo không giảm quá tổng hóa đơn
        return Math.min(discount, orderTotal);
    }
}
