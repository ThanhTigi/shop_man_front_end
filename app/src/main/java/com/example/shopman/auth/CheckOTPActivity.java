package com.example.shopman.auth;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.shopman.R;
import com.example.shopman.models.changepassword.ForgotPasswordResponse;
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
        setContentView(R.layout.activity_check_otp);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        etOtp = findViewById(R.id.etOtp);
        tvResend = findViewById(R.id.tvResend);
        btnVerify = findViewById(R.id.btnVerify);
        apiManager = new ApiManager();

        // Set up back button
        ivBack.setOnClickListener(v -> finish());

        // Set up countdown timer for Resend button
        startCountdownTimer();

        // Set up Resend button click listener
        tvResend.setOnClickListener(v -> {
            if (!isTimerRunning) {

                apiManager.forgotPassword(email, new ApiResponseListener<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse response) {
                        // Chuyển đến màn hình chính
                        startCountdownTimer();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Hiển thị lỗi
                        Toast.makeText(CheckOTPActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
                apiManager.checkOTP(otp, new ApiResponseListener<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse response) {
                        Intent otpIntent = new Intent(CheckOTPActivity.this, ChangePasswordActivity.class);
                        otpIntent.putExtra("resetToken", response.getMetaData().getMessage()); // Pass the email as an extra
                        startActivity(otpIntent);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Hiển thị lỗi
                        Toast.makeText(CheckOTPActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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