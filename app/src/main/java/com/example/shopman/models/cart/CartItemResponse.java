package com.example.shopman.models.cart;

import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class CartItemResponse {
    @SerializedName("field")
    private String field;

    @SerializedName("productId")
    private int productId;

    @SerializedName("productName")
    private String productName;

    @SerializedName("skuNo")
    private String skuNo;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("price")
    private String price;

    @SerializedName("image")
    private String image;

    @SerializedName("variant")
    private Map<String, Object> variant;

    @SerializedName("discounts")
    private List<Discount> discounts;

    private boolean isSelected;

    public Product toProduct() {
        long priceValue;
        try {
            priceValue = Long.parseLong(price);
        } catch (NumberFormatException e) {
            priceValue = 0L; // Fallback khi price không hợp lệ
        }
        Product product = new Product(String.valueOf(productId), productName, priceValue, image, quantity, skuNo, variant);
        product.setDiscounts(discounts);
        product.setSelected(isSelected);
        return product;
    }

    // Getters and setters
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSkuNo() { return skuNo; }
    public void setSkuNo(String skuNo) { this.skuNo = skuNo; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Map<String, Object> getVariant() { return variant; }
    public void setVariant(Map<String, Object> variant) { this.variant = variant; }
    public List<Discount> getDiscounts() { return discounts; }
    public void setDiscounts(List<Discount> discounts) { this.discounts = discounts; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}