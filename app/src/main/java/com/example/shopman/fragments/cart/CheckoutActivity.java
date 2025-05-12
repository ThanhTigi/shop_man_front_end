package com.example.shopman.fragments.cart;

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

import com.example.shopman.R;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvSelectCoupon, tvOrderAmount, tvDeliveryFee;
    private TextView tvOriginalAmount, tvDiscountAmount, tvOrderTotal, tvFinalTotal;
    private Button btnProceedToPayment;
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> selectedCartItems;
    private List<Coupon> couponList;
    private double orderTotal;
    private double discount = 0.0;
    private AlertDialog couponDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ivBack = findViewById(R.id.ivBack);
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);
        tvSelectCoupon = findViewById(R.id.tvSelectCoupon);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvOriginalAmount = findViewById(R.id.tvOriginalAmount);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        tvFinalTotal = findViewById(R.id.tvFinalTotal);
        btnProceedToPayment = findViewById(R.id.btnProceedToPayment);

        selectedCartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        if (selectedCartItems == null || selectedCartItems.isEmpty()) {
            Toast.makeText(this, "No items selected for checkout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        checkoutAdapter = new CheckoutAdapter(selectedCartItems);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        orderTotal = 0.0;
        for (CartItem item : selectedCartItems) {
            orderTotal += item.getTotalPrice();
        }

        couponList = new ArrayList<>();
        couponList.add(new Coupon("SUMMER500", "31 May 2025", 10, 50.0)); // 50% off
        couponList.add(new Coupon("WINTER200", "15 Dec 2025", 20, 20.0)); // 20% off
        couponList.add(new Coupon("FESTIVE1000", "1 Jan 2026", 5, 80.0)); // 80% off

        tvOrderAmount.setText("đ" + String.format("%.2f", orderTotal));
        tvDeliveryFee.setText("FREE");
        updateOrderTotal();

        ivBack.setOnClickListener(v -> finish());


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
                couponDialog.dismiss();
            }
        });
        rvCoupons.setAdapter(couponAdapter);

        builder.setView(dialogView);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        couponDialog = builder.create();
        couponDialog.show();
    }

    private void applyCoupon(Coupon coupon) {
        discount = coupon.calculateDiscount(orderTotal);
        tvSelectCoupon.setText(coupon.getCode());
        updateOrderTotal();
    }

    private void updateOrderTotal() {
        double finalTotal = orderTotal - discount;
        tvOriginalAmount.setText("đ" + String.format("%.2f", orderTotal));
        tvDiscountAmount.setText("-đ" + String.format("%.2f", discount));
        tvOrderTotal.setText("đ" + String.format("%.2f", finalTotal));
        tvFinalTotal.setText("đ" + String.format("%.2f", finalTotal));
    }
}