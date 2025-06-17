package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shopman.R;
import com.example.shopman.models.CampaignResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.response.ForgotPasswordResponse;
import com.example.shopman.models.checkotp.CheckOTPResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class CheckOTPActivity extends AppCompatActivity {

    private String email;
    private ImageView ivBack;
    private EditText etOtp;
    private TextView tvResend;
    private AppCompatButton btnVerify;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_check_otp);

        // Thêm padding động cho LinearLayout root
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight); // Padding trên và dưới
            return insets;
        });

        // Lấy email từ Intent
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        etOtp = findViewById(R.id.etOtp);
        tvResend = findViewById(R.id.tvResend);
        btnVerify = findViewById(R.id.btnVerify);
        apiManager = new ApiManager(this);

        // Set up back button
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        } else {
            // Log nếu ivBack không tồn tại, phòng trường hợp layout thay đổi
            android.util.Log.e("CheckOTPActivity", "ivBack not found in layout");
        }

        // Set up countdown timer for Resend button
        startCountdownTimer();

        // Set up Resend button click listener
        tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {
                ForgotPasswordRequest request = new ForgotPasswordRequest(email);
                apiManager.forgotPassword(request, new ApiResponseListener<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse response) {
                        Toast.makeText(CheckOTPActivity.this, "OTP resent successfully", Toast.LENGTH_SHORT).show();
                        startCountdownTimer();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(CheckOTPActivity.this, "Failed to resend OTP: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set up Verify button click listener
        btnVerify.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            } else if (otp.length() != 6) {
                Toast.makeText(this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
            } else {
                OTPRequest request = new OTPRequest(otp);
                apiManager.checkOTP(request, new ApiResponseListener<CheckOTPResponse>() {
                    @Override
                    public void onSuccess(CheckOTPResponse response) {
                        String resetToken = response.getMetadata().getInnerMetadata().getResetToken();
                        Intent otpIntent = new Intent(CheckOTPActivity.this, ChangePasswordActivity.class);
                        otpIntent.putExtra("resetToken", resetToken);
                        startActivity(otpIntent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(CheckOTPActivity.this, "Failed to verify OTP: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startCountdownTimer() {
        tvResend.setEnabled(false);
        isTimerRunning = true;
        countDownTimer = new CountDownTimer(60000, 1000) { // 60 seconds, tick every 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                tvResend.setText("Resend (" + secondsRemaining + "s)");
            }

            @Override
            public void onFinish() {
                tvResend.setText("Resend");
                tvResend.setEnabled(true);
                isTimerRunning = false;
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}