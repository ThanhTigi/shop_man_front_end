package com.example.shopman;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvOrderAmount, tvShippingFee, tvTotalAmount;
    private LinearLayout llVisa, llPaypal, llOther1, llOther2;
    private Button btnContinue;
    private double orderTotal;
    private List<CartItem> selectedCartItems;
    private int selectedPaymentMethod = -1; // -1 means no method selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        llVisa = findViewById(R.id.llVisa);
        llPaypal = findViewById(R.id.llPaypal);
        llOther1 = findViewById(R.id.llOther1);
        llOther2 = findViewById(R.id.llOther2);
        btnContinue = findViewById(R.id.btnContinue);

        // Get data from Intent
        orderTotal = getIntent().getDoubleExtra("orderTotal", 0.0);
        selectedCartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");

        // Calculate shipping fee (hardcoded for now)
        double shippingFee = 30.0;
        double totalWithShipping = orderTotal + shippingFee;

        // Update order summary
        tvOrderAmount.setText("₹" + String.format("%.2f", orderTotal));
        tvShippingFee.setText("₹" + String.format("%.2f", shippingFee));
        tvTotalAmount.setText("₹" + String.format("%.2f", totalWithShipping));

        // Set up click listeners for payment methods
        llVisa.setOnClickListener(v -> selectPaymentMethod(0));
        llPaypal.setOnClickListener(v -> selectPaymentMethod(1));
        llOther1.setOnClickListener(v -> selectPaymentMethod(2));
        llOther2.setOnClickListener(v -> selectPaymentMethod(3));

        // Set up back button
        ivBack.setOnClickListener(v -> finish());

        // Set up Continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedPaymentMethod == -1) {
                Toast.makeText(PaymentActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else {
                showPaymentSuccessDialog();
            }
        });
    }

    private void selectPaymentMethod(int method) {
        // Reset all backgrounds
        llVisa.setBackgroundColor(Color.WHITE);
        llPaypal.setBackgroundColor(Color.WHITE);
        llOther1.setBackgroundColor(Color.WHITE);
        llOther2.setBackgroundColor(Color.WHITE);

        // Highlight the selected method
        selectedPaymentMethod = method;
        switch (method) {
            case 0:
                llVisa.setBackgroundColor(Color.parseColor("#FFF0F0")); // Light red background
                break;
            case 1:
                llPaypal.setBackgroundColor(Color.parseColor("#FFF0F0"));
                break;
            case 2:
                llOther1.setBackgroundColor(Color.parseColor("#FFF0F0"));
                break;
            case 3:
                llOther2.setBackgroundColor(Color.parseColor("#FFF0F0"));
                break;
        }
    }

    private void showPaymentSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_payment_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        // Show the dialog
        dialog.show();

        // Automatically dismiss the dialog after 2 seconds and navigate to HomeActivity
        new android.os.Handler().postDelayed(() -> {
            dialog.dismiss();
            Intent homeIntent = new Intent(PaymentActivity.this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        }, 2000);
    }
}