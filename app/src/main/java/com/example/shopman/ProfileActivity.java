package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopman.auth.LoginActivity;
import com.example.shopman.models.login.UserMetadata;
import com.example.shopman.models.profile.getuserprofile.Address;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileAddress;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileUser;
import com.example.shopman.models.profile.getuserprofile.UserProfileMetadata;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivProfileImage;
    private EditText etEmail, etName, etPhoneNumber, etAddress, etPincode, etCity, etCountry;
    private Button btnSave;

    private String accessToken;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivBack = findViewById(R.id.ivBack);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        etPincode = findViewById(R.id.etPincode);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        btnSave = findViewById(R.id.btnSave);

        UserMetadata userMetadata = UserMetadata.fromJson(MyPreferences.getString(ProfileActivity.this, "current_user_meta_data", ""));
        ApiManager apiManager = new ApiManager();
        accessToken = userMetadata.getTokens().getAccessToken();
        apiManager.getUserProfile(accessToken, new ApiResponseListener<GetUserProfileResponse>() {
            @Override
            public void onSuccess(GetUserProfileResponse response) {
                UserProfileMetadata user = response.getUserProfileMetaData().getUserProfileMetaData();
                etEmail.setText(user.getEmail());
                etName.setText(user.getName());
                etPhoneNumber.setText(user.getPhone());
                avatar = user.getAvatar();
                if (!user.getAddress().isEmpty()) {
                    Address address = user.getAddress().get(0);
                    etPincode.setText(String.valueOf(address.getPincode()));
                    etAddress.setText(address.getAddress());
                    etCity.setText(address.getCity() );
                    etCountry.setText(address.getCountry());
                } else {
                    etPincode.setText("");
                    etAddress.setText("");
                    etCity.setText("");
                    etCountry.setText("");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProfileActivity.this, "User Profile fail!", Toast.LENGTH_SHORT).show();
                MyPreferences.setString(ProfileActivity.this, "current_user_meta_data", "");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        ivBack.setOnClickListener(v -> finish());


        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            String pincode = etPincode.getText().toString();
            String address = etAddress.getText().toString();
            String city = etCity.getText().toString();
            String country = etCountry.getText().toString();

            if (name.isEmpty() || phoneNumber.isEmpty() || pincode.isEmpty() || address.isEmpty() || city.isEmpty()|| country.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
            else if (!isInteger(pincode))
            {
                Toast.makeText(ProfileActivity.this, "Pincode must be a number", Toast.LENGTH_SHORT).show();
            }
            else {
                UpdateProfileUser user = new UpdateProfileUser(name,phoneNumber,avatar);
                UpdateProfileAddress address1 = new UpdateProfileAddress("main",Integer.parseInt(pincode),address,city,country);
                UpdateProfileRequest request = new UpdateProfileRequest(user, address1);
                apiManager.updateUserProfile(accessToken, request, new ApiManager.BooleanCallback() {
                    @Override
                    public void onResult(boolean result) {
                        if (result)
                        {
                            Toast.makeText(ProfileActivity.this, "Update User Profile success!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, "Update User Profile fail!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}