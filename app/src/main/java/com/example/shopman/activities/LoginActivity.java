package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.remote.RetrofitClient;
import com.example.shopman.utilitis.MyPreferences;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageView ivShowPassword;
    private ImageButton googleButton;
    private boolean isPasswordVisible = false;
    private ApiManager apiManager;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight);
            return insets;
        });

        RetrofitClient.init(this);
        apiManager = new ApiManager(this);

        String accessToken = MyPreferences.getString(this, "access_token", null);
        String refreshToken = MyPreferences.getString(this, "refresh_token", null);
        if (accessToken != null && !accessToken.isEmpty() && refreshToken != null && !refreshToken.isEmpty()) {
            Log.d(TAG, "Access token found: " + accessToken);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "Google Play Services unavailable");
            Toast.makeText(this, "Google Play Services is required for Google Sign-In", Toast.LENGTH_LONG).show();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("629917845564-d6b1672h9s4uhi43dmfhpsjtg91hsmih.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        ivShowPassword = findViewById(R.id.ivShowPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView btnForgotPassWord = findViewById(R.id.btnForgotPassword);
        TextView btnCreateAccount = findViewById(R.id.btnCreateAccount);
        googleButton = findViewById(R.id.googleButton);

        ivShowPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivShowPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivShowPassword.setImageResource(android.R.drawable.ic_menu_view);
            }
            etPassword.setSelection(etPassword.getText().length());
            Log.d(TAG, "Password visibility toggled: " + isPasswordVisible);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etUsername.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Login attempt with empty fields");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(LoginActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Invalid email format: " + email);
                return;
            }
            if (!email.endsWith(".vn")) {
                Toast.makeText(LoginActivity.this, "Please enter an email ending with .vn", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Email does not end with .vn: " + email);
                return;
            }

            Log.d(TAG, "Attempting login with email: " + email);
            apiManager.login(email, password, new ApiResponseListener<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse response) {
                    if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null
                            && response.getMetadata().getMetadata().getTokens() != null) {
                        String accessToken = response.getMetadata().getMetadata().getTokens().getAccessToken();
                        String refreshToken = response.getMetadata().getMetadata().getTokens().getRefreshToken();
                        String avatar = response.getMetadata().getMetadata().getUser() != null
                                ? response.getMetadata().getMetadata().getUser().getAvatar() : null;
                        MyPreferences.setString(LoginActivity.this, "access_token", accessToken);
                        MyPreferences.setString(LoginActivity.this, "refresh_token", refreshToken);
                        Log.d(TAG, "Login successful: " + new Gson().toJson(response));
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user_avatar", avatar);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // Gửi broadcast để thông báo cập nhật thông tin người dùng
                        LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(
                                new Intent("com.example.shopman.ACTION_UPDATE_USER")
                        );
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Invalid response from server: " + (response != null ? new Gson().toJson(response) : "null"));
                        Toast.makeText(LoginActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Login failed: " + errorMessage);
                    Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        googleButton.setOnClickListener(v -> {
            Log.d(TAG, "Google Sign-In button clicked");
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        btnForgotPassWord.setOnClickListener(v -> {
            Log.d(TAG, "Forgot Password clicked");
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnCreateAccount.setOnClickListener(v -> {
            Log.d(TAG, "Create Account clicked");
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d(TAG, "Google Sign-In successful, account: " + account);
                if (account != null) {
                    String idToken = account.getIdToken();
                    if (idToken != null) {
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
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null
                        && response.getMetadata().getMetadata().getTokens() != null) {
                    String accessToken = response.getMetadata().getMetadata().getTokens().getAccessToken();
                    String refreshToken = response.getMetadata().getMetadata().getTokens().getRefreshToken();
                    String avatar = response.getMetadata().getMetadata().getUser() != null
                            ? response.getMetadata().getMetadata().getUser().getAvatar() : null;
                    MyPreferences.setString(LoginActivity.this, "access_token", accessToken);
                    MyPreferences.setString(LoginActivity.this, "refresh_token", refreshToken);
                    Log.d(TAG, "Google login successful: " + new Gson().toJson(response));
                    Toast.makeText(LoginActivity.this, "Google login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_avatar", avatar);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Gửi broadcast để thông báo cập nhật thông tin người dùng
                    LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(
                            new Intent("com.example.shopman.ACTION_UPDATE_USER")
                    );
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Invalid response from server: " + (response != null ? new Gson().toJson(response) : "null"));
                    Toast.makeText(LoginActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Google login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Google login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(this);
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (googleApi.isUserResolvableError(resultCode)) {
                googleApi.getErrorDialog(this, resultCode, 0).show();
            }
            return false;
        }
        return true;
    }
}