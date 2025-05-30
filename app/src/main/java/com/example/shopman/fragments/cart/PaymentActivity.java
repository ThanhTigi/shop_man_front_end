package com.example.shopman.fragments.cart;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.utilitis.MyPreferences;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvOrderAmount, tvShippingFee, tvTotalAmount;
    private LinearLayout llVisa, llPaypal, llOther1, llOther2;
    private Button btnContinue;
    private double orderTotal;
    private List<CartItem> selectedCartItems;
    private int selectedPaymentMethod = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ivBack = findViewById(R.id.ivBack);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        llVisa = findViewById(R.id.llVisa);
        llPaypal = findViewById(R.id.llPaypal);
        llOther1 = findViewById(R.id.llOther1);
        llOther2 = findViewById(R.id.llOther2);
        btnContinue = findViewById(R.id.btnContinue);


        orderTotal = getIntent().getDoubleExtra("orderTotal", 0.0);
        selectedCartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("selectedCartItems");

        double shippingFee = 30.0;
        double totalWithShipping = orderTotal + shippingFee;

        tvOrderAmount.setText(String.format("%.2f", orderTotal) + "đ");
        tvShippingFee.setText(String.format("%.2f", shippingFee) + "đ");
        tvTotalAmount.setText(String.format("%.2f", totalWithShipping) + "đ");

        llVisa.setOnClickListener(v -> showCardInfoDialog(0, "VISA", llVisa, R.id.tvVisaInfo, R.id.tvVisaHolderName, R.id.tvVisaCardNumber, R.id.tvVisaExpiryDate));
        llPaypal.setOnClickListener(v -> showCardInfoDialog(1, "PayPal", llPaypal, R.id.tvPaypalInfo, R.id.tvPaypalHolderName, R.id.tvPaypalCardNumber, R.id.tvPaypalExpiryDate));
        llOther1.setOnClickListener(v -> showCardInfoDialog(2, "Mastercard", llOther1, R.id.tvMastercardInfo, R.id.tvMastercardHolderName, R.id.tvMastercardCardNumber, R.id.tvMastercardExpiryDate));
        llOther2.setOnClickListener(v -> showCardInfoDialog(3, "Apple Pay", llOther2, R.id.tvApplePayInfo, R.id.tvApplePayHolderName, R.id.tvApplePayCardNumber, R.id.tvApplePayExpiryDate));

        ivBack.setOnClickListener(v -> finish());

        btnContinue.setOnClickListener(v -> {
            if (selectedPaymentMethod == -1) {
                Toast.makeText(PaymentActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else {
                showPaymentSuccessDialog();
            }
        });
    }

    private void showCardInfoDialog(int method, String methodName, LinearLayout selectedLayout, int infoTextId, int holderNameTextId, int cardNumberTextId, int expiryDateTextId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_credit_card, null);
        EditText etCardHolderName = dialogView.findViewById(R.id.etCardHolderName);
        EditText etCardNumber = dialogView.findViewById(R.id.etCardNumber);
        DatePicker dpExpiryDate = dialogView.findViewById(R.id.dpExpiryDate);
        Button btnDone = dialogView.findViewById(R.id.btnDone);

        dpExpiryDate.setMinDate(System.currentTimeMillis());

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnDone.setOnClickListener(v -> {
            String cardHolderName = etCardHolderName.getText().toString().trim();
            String cardNumber = etCardNumber.getText().toString().trim();
            int month = dpExpiryDate.getMonth() + 1; // DatePicker month is 0-based
            int year = dpExpiryDate.getYear();
            String expiryDate = String.format("%02d/%d", month, year % 100); // Format as MM/YY

            // Validate inputs
            if (cardHolderName.isEmpty()) {
                Toast.makeText(this, "Please enter card holder name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardNumber.length() != 16) {
                Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            MyPreferences.setString(this,methodName + "_holder", cardHolderName);
            MyPreferences.setString(this,methodName + "_number", cardNumber);
            MyPreferences.setString(this,methodName + "_expiry", expiryDate);

            TextView tvInfo = findViewById(infoTextId);
            TextView tvHolderName = findViewById(holderNameTextId);
            TextView tvCardNumber = findViewById(cardNumberTextId);
            TextView tvExpiryDate = findViewById(expiryDateTextId);

            tvInfo.setText(methodName);

            String holderText = "Họ tên: " + cardHolderName;
            SpannableString holderSpannable = new SpannableString(holderText);
            holderSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, "Họ tên:".length(), 0);
            tvHolderName.setText(holderSpannable);

            String cardText = "Mã thẻ: **** **** **** " + cardNumber.substring(cardNumber.length() - 4);
            SpannableString cardSpannable = new SpannableString(cardText);
            cardSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, "Mã thẻ:".length(), 0);
            tvCardNumber.setText(cardSpannable);

            String expiryText = "Ngày hết hạn: " + expiryDate;
            SpannableString expirySpannable = new SpannableString(expiryText);
            expirySpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, "Ngày hết hạn:".length(), 0);
            tvExpiryDate.setText(expirySpannable);

            selectedPaymentMethod = method;
            llVisa.setVisibility(method == 0 ? View.VISIBLE : View.GONE);
            llPaypal.setVisibility(method == 1 ? View.VISIBLE : View.GONE);
            llOther1.setVisibility(method == 2 ? View.VISIBLE : View.GONE);
            llOther2.setVisibility(method == 3 ? View.VISIBLE : View.GONE);

            selectedLayout.setBackgroundColor(Color.parseColor("#FFF0F0"));

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showPaymentSuccessDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Payment Success")
                .setMessage("Your payment has been processed successfully!")
                .setPositiveButton("OK", (d, which) -> {
                    Intent homeIntent = new Intent(PaymentActivity.this, MainActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(homeIntent);
                    finish();
                })
                .setCancelable(false)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}