package com.example.shopman;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private boolean isSelected;
    private String selectedVariation;

    public CartItem(Product product, int quantity, boolean isSelected, String selectedVariation) {
        this.product = product;
        this.quantity = quantity;
        this.isSelected = isSelected;
        this.selectedVariation = selectedVariation;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSelectedVariation() {
        return selectedVariation;
    }

    public void setSelectedVariation(String selectedVariation) {
        this.selectedVariation = selectedVariation;
    }

    public double getTotalPrice() {
        // Assuming the price is in the format "$34.00", extract the numeric value
        String priceStr = product.getPrice().replace("$", "").trim();
        double price = Double.parseDouble(priceStr);
        return price * quantity;
    }
}