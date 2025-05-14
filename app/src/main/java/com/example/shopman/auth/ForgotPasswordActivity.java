package com.example.shopman.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.R;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etEmail;
    private Button btnSubmit;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        ivBack = findViewById(R.id.ivBack);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        apiManager = new ApiManager(this);

        // Back Button Click
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Submit Button Click
        btnSubmit.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            // Basic Validation
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                ForgotPasswordRequest request = new ForgotPasswordRequest(email);
                apiManager.forgotPassword(request, new ApiResponseListener<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse response) {
                        Toast.makeText(ForgotPasswordActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        Intent otpIntent = new Intent(ForgotPasswordActivity.this, CheckOTPActivity.class);
                        otpIntent.putExtra("email", email);
                        startActivity(otpIntent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}