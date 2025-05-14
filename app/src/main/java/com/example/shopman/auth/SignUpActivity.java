package com.example.shopman.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSignUpUsername, etSignUpName, etSignUpPassword, etConfirmPassword;
    private ImageView ivShowSignUpPassword, ivShowConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        etSignUpUsername = findViewById(R.id.etSignUpUsername);
        etSignUpName = findViewById(R.id.etSignUpName);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivShowSignUpPassword = findViewById(R.id.ivShowSignUpPassword);
        ivShowConfirmPassword = findViewById(R.id.ivShowConfirmPassword);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        apiManager = new ApiManager(this);

        // Toggle Password Visibility
        ivShowSignUpPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etSignUpPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                etSignUpPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_view);
            }
            etSignUpPassword.setSelection(etSignUpPassword.getText().length());
        });

        // Toggle Confirm Password Visibility
        ivShowConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // Create Account Button Click
        btnCreateAccount.setOnClickListener(v -> {
            String email = etSignUpUsername.getText().toString().trim();
            String name = etSignUpName.getText().toString().trim();
            String password = etSignUpPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Basic Validation
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(SignUpActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                SignUpRequest request = new SignUpRequest(name, email, password);
                apiManager.signUp(request, new ApiResponseListener<SignUpResponse>() {
                    @Override
                    public void onSuccess(SignUpResponse response) {
                        Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(SignUpActivity.this, "Sign up failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Navigate to Login Screen
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}