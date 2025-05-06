package com.example.shopman.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.R;
import com.example.shopman.models.SignUpResponse;
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
        apiManager = new ApiManager();


        // Toggle Password Visibility
        ivShowSignUpPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etSignUpPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                    ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    etSignUpPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivShowSignUpPassword.setImageResource(android.R.drawable.ic_menu_view);
                }
                etSignUpPassword.setSelection(etSignUpPassword.getText().length());
            }
        });

        // Toggle Confirm Password Visibility
        ivShowConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                    ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        });

        // Create Account Button Click
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etSignUpUsername.getText().toString().trim();
                String name = etSignUpName.getText().toString().trim();
                String password = etSignUpPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // Basic Validation
                if (username.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Add your sign-up logic here (e.g., save to a database or send to a server)


                    apiManager.signUp(username,name, password, new ApiResponseListener<SignUpResponse>() {
                        @Override
                        public void onSuccess(SignUpResponse response) {
                            // Chuyển đến màn hình chính
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Hiển thị lỗi
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Navigate to Login Screen
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}