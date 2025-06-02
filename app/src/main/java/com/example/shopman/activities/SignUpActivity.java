package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.gson.Gson;

public class SignUpActivity extends AppCompatActivity {

    private EditText etSignUpUsername, etSignUpName, etSignUpPassword, etConfirmPassword;
    private ImageView ivShowSignUpPassword, ivShowConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private ApiManager apiManager;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignUpActivity";

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
        ImageView googleButton = findViewById(R.id.google_icon); // Sửa: googleButton thành google_icon
        apiManager = new ApiManager(this);

        // Khởi tạo Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("629917845564-d6b1672h9s4uhi43dmfhpsjtg91hsmih.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

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
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT); // Sửa: etSignUpPassword thành etConfirmPassword
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD); // Sửa
                ivShowConfirmPassword.setImageResource(android.R.drawable.ic_menu_view);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length()); // Sửa
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

        // Google Sign-In Button Click
        googleButton.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Navigate to Login Screen
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: RC_SIGN_IN, resultCode=" + resultCode + ", data=" + (data != null));
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    Log.d(TAG, "Google Sign-In successful, account: email=" + account.getEmail() + ", idToken=" + idToken);
                    if (idToken != null) {
                        Log.d(TAG, "Google Sign-In successful, idToken: " + idToken);
                        loginWithGoogle(idToken);
                    } else {
                        Log.e(TAG, "Google Sign-In failed: No idToken");
                        Toast.makeText(this, "Google Sign-In failed: No idToken", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Google Sign-In failed: No account");
                    Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In error, statusCode: " + e.getStatusCode() + ", message: " + e.getMessage());
                Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginWithGoogle(String idToken) {
        Log.d(TAG, "Sending idToken to server");
        apiManager.loginWithGoogle(idToken, new ApiResponseListener<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata().getTokens() != null) {
                    Log.d(TAG, "Google login successful: " + new Gson().toJson(response));
                    Toast.makeText(SignUpActivity.this, "Google login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Invalid response from server: " + (response != null ? new Gson().toJson(response) : "null"));
                    Toast.makeText(SignUpActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Google login failed: " + errorMessage);
                Toast.makeText(SignUpActivity.this, "Google login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}