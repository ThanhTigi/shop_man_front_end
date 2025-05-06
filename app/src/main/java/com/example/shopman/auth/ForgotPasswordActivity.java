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
import com.example.shopman.models.ForgotPasswordResponse;
import com.example.shopman.models.SignUpResponse;
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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        apiManager = new ApiManager();

        // Submit Button Click
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                // Basic Validation
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {

                    apiManager.forgotPassword(email, new ApiResponseListener<ForgotPasswordResponse>() {
                        @Override
                        public void onSuccess(ForgotPasswordResponse response) {
                            // Chuyển đến màn hình chính
                            Intent otpIntent = new Intent(ForgotPasswordActivity.this, CheckOTPActivity.class);
                            otpIntent.putExtra("email", email); // Pass the email as an extra
                            startActivity(otpIntent);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Hiển thị lỗi
                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        // Nếu không gọi super.onBackPressed(), Activity sẽ không đóng
        super.onBackPressed();
    }

    // Simple email validation
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}