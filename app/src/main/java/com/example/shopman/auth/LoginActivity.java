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

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.SharedPreferencesManager;
import com.example.shopman.models.LoginResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageView ivShowPassword;
    private boolean isPasswordVisible = false;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivShowPassword = findViewById(R.id.ivShowPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnForgotPassWord = findViewById(R.id.btnForgotPassword);
        TextView btnCreateAccount = findViewById(R.id.btnCreateAccount);

        apiManager = new ApiManager();

        // Toggle Password Visibility
        ivShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                    ivShowPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                } else {
                    etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivShowPassword.setImageResource(android.R.drawable.ic_menu_view);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        // Login Button Click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Add your login logic here (e.g., validate with a server)
                    apiManager.login(username, password, new ApiResponseListener<LoginResponse>() {
                        @Override
                        public void onSuccess(LoginResponse response) {
                            // Lưu thông tin người dùng vào SharedPreferences
                            SharedPreferencesManager.saveUserInfo(LoginActivity.this, response.getMetadata().getUser());
                            // Chuyển đến màn hình chính
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Hiển thị lỗi
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        btnForgotPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}