package com.example.shopman.auth;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.shopman.R;
import com.example.shopman.models.changepassword.response.ChangePasswordResponse;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etSignUpPassword, etConfirmPassword;
    private ImageView ivShowSignUpPassword, ivShowConfirmPassword;
    private AppCompatButton btnCreateAccount;
    private boolean isSignUpPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private ApiManager apiManager;

    private String resetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ivBack = findViewById(R.id.ivBack);
        etSignUpPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivShowSignUpPassword = findViewById(R.id.ivShowPassword);
        ivShowConfirmPassword = findViewById(R.id.ivShowConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnChangePassword);
        apiManager = new ApiManager();
        resetToken = getIntent().getStringExtra("resetToken");
        ivBack.setOnClickListener(v -> finish());

        ivShowSignUpPassword.setOnClickListener(v -> {
            isSignUpPasswordVisible = !isSignUpPasswordVisible;
            if (isSignUpPasswordVisible) {
                etSignUpPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Eye-off icon
            } else {
                etSignUpPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_view); // Eye-on icon
            }
            etSignUpPassword.setSelection(etSignUpPassword.getText().length());
        });

        ivShowConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Eye-off icon
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view); // Eye-on icon
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnCreateAccount.setOnClickListener(v -> {
            String password = etSignUpPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                apiManager.changePassword(resetToken, password, confirmPassword, new ApiResponseListener<ChangePasswordResponse>() {
                    @Override
                    public void onSuccess(ChangePasswordResponse response) {
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}