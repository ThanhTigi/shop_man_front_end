package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopman.R;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.response.ForgotPasswordResponse;
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
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_forgot_password);

        // Thêm padding động cho LinearLayout root
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight); // Padding trên và dưới
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        apiManager = new ApiManager(this);

        // Back Button Click
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        } else {
            // Log nếu ivBack không tồn tại, phòng trường hợp layout thay đổi
            android.util.Log.e("ForgotPasswordActivity", "ivBack not found in layout");
        }

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