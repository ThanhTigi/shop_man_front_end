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

public class CheckoutActivity extends AppCompatActivity {

    private ImageView ivBack, ivWishlist;
    private TextView tvSelectCoupon, tvOrderAmount, tvConvenienceFee, tvConvenienceFeeValue, tvDeliveryFee;
    private TextView tvOrderTotal, tvFinalTotal;
    private Button btnProceedToPayment;
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartItem> selectedCartItems;
    private double orderTotal;

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

        // Update order payment details
        tvOrderAmount.setText("₹" + String.format("%.2f", orderTotal));
        tvConvenienceFee.setText("Know More");
        tvConvenienceFeeValue.setText("Apply Coupon");
        tvDeliveryFee.setText("FREE");
        tvOrderTotal.setText("₹" + String.format("%.2f", orderTotal));
        tvFinalTotal.setText("₹" + String.format("%.2f", orderTotal));

        // Set up click listeners
        ivBack.setOnClickListener(v -> finish());

        ivWishlist.setOnClickListener(v -> Toast.makeText(CheckoutActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show());

        tvSelectCoupon.setOnClickListener(v -> Toast.makeText(CheckoutActivity.this, "Select Coupon clicked", Toast.LENGTH_SHORT).show());

        btnProceedToPayment.setOnClickListener(v -> {
            Intent paymentIntent = new Intent(CheckoutActivity.this, PaymentActivity.class);
            paymentIntent.putExtra("orderTotal", orderTotal);
            paymentIntent.putExtra("selectedCartItems", new ArrayList<>(selectedCartItems));
            startActivity(paymentIntent);
        });
    }
}