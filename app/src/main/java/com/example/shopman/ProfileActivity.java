package com.example.shopman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivCheckout, ivProfileImage;
    private EditText etEmail, etPassword, etFlatNo, etAddress, etCity, etZipCode, etCountry;
    private EditText etAccountNumber, etAccountHolderName, etIfscCode;
    private TextView tvChangePassword;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        ivCheckout = findViewById(R.id.ivCheckout);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFlatNo = findViewById(R.id.etFlatNo);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etZipCode = findViewById(R.id.etZipCode);
        etCountry = findViewById(R.id.etCountry);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etAccountHolderName = findViewById(R.id.etAccountHolderName);
        etIfscCode = findViewById(R.id.etIfscCode);
        tvChangePassword = findViewById(R.id.tvChangePassword);
        btnSave = findViewById(R.id.btnSave);

        // Set up click listeners
        ivBack.setOnClickListener(v -> finish()); // Go back to previous activity

        ivCheckout.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Checkout clicked", Toast.LENGTH_SHORT).show());

        tvChangePassword.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Change Password clicked", Toast.LENGTH_SHORT).show());

        btnSave.setOnClickListener(v -> {
            // Validate and save profile details
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String flatNo = etFlatNo.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String zipCode = etZipCode.getText().toString().trim();
            String country = etCountry.getText().toString().trim();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String accountHolderName = etAccountHolderName.getText().toString().trim();
            String ifscCode = etIfscCode.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || flatNo.isEmpty() || address.isEmpty() ||
                    city.isEmpty() || zipCode.isEmpty() || country.isEmpty() || accountNumber.isEmpty() ||
                    accountHolderName.isEmpty() || ifscCode.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}