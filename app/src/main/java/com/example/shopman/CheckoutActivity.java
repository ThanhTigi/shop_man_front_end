package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


import androidx.appcompat.app.AlertDialog;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView ivBack, ivWishlist;
    private TextView tvSelectCoupon, tvOrderAmount, tvConvenienceFee, tvConvenienceFeeValue, tvDeliveryFee;
    private TextView tvOriginalAmount, tvDiscountAmount, tvOrderTotal, tvFinalTotal;
    private Button btnProceedToPayment;
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> selectedCartItems;
    private List<Coupon> couponList;
    private double orderTotal;
    private double discount = 0.0;
    private AlertDialog couponDialog; // Để quản lý dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        ivWishlist = findViewById(R.id.ivWishlist);
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);
        tvSelectCoupon = findViewById(R.id.tvSelectCoupon);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvConvenienceFee = findViewById(R.id.tvConvenienceFee);
        tvConvenienceFeeValue = findViewById(R.id.tvConvenienceFeeValue);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvOriginalAmount = findViewById(R.id.tvOriginalAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment);

        // Get selected cart items from Intent
        selectedCartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        checkoutAdapter = new CheckoutAdapter(selectedCartItems);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        // Calculate order total
        orderTotal = 0.0;
        for (CartItem item : selectedCartItems) {
            orderTotal += item.getTotalPrice();
        }

        // Initialize coupon list with percentage discounts (0% - 80%)
        couponList = new ArrayList<>();
        couponList.add(new Coupon("SUMMER500", "31 May 2025", 10, 50.0)); // 50% off
        couponList.add(new Coupon("WINTER200", "15 Dec 2025", 20, 20.0)); // 20% off
        couponList.add(new Coupon("FESTIVE1000", "1 Jan 2026", 5, 80.0)); // 80% off

        // Update order payment details
        tvOrderAmount.setText("₹" + String.format("%.2f", orderTotal));
        tvConvenienceFee.setText("Know More");
        tvConvenienceFeeValue.setText("Apply Coupon");
        tvDeliveryFee.setText("FREE");
        updateOrderTotal();

        // Set up click listeners
        ivBack.setOnClickListener(v -> finish());

        ivWishlist.setOnClickListener(v -> Toast.makeText(CheckoutActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show());

        tvSelectCoupon.setOnClickListener(v -> showCouponDialog());

        btnProceedToPayment.setOnClickListener(v -> {
            Intent paymentIntent = new Intent(CheckoutActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("orderTotal", orderTotal - discount);
            paymentIntent.putExtra("selectedCartItems", new ArrayList<>(selectedCartItems));
            startActivity(paymentIntent);
        });
    }

    private void showCouponDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Coupon");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_coupon_list, null);
        RecyclerView rvCoupons = dialogView.findViewById(R.id.rvCoupons);
        rvCoupons.setLayoutManager(new LinearLayoutManager(this));
        CouponAdapter couponAdapter = new CouponAdapter(couponList, coupon -> {
            applyCoupon(coupon);
            if (couponDialog != null) {
                couponDialog.dismiss(); // Đóng dialog sau khi chọn
            }
        });
        rvCoupons.setAdapter(couponAdapter);

        builder.setView(dialogView);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        couponDialog = builder.create();
        couponDialog.show();
    }

    private void applyCoupon(Coupon coupon) {
        discount = coupon.calculateDiscount(orderTotal); // Tính số tiền giảm
        tvSelectCoupon.setText(coupon.getCode());
        updateOrderTotal();
    }

    private void updateOrderTotal() {
        double finalTotal = orderTotal - discount;
        tvOriginalAmount.setText("₹" + String.format("%.2f", orderTotal));
        tvDiscountAmount.setText("-₹" + String.format("%.2f", discount));
        tvOrderTotal.setText("₹" + String.format("%.2f", finalTotal));
        tvFinalTotal.setText("₹" + String.format("%.2f", finalTotal));
    }
}