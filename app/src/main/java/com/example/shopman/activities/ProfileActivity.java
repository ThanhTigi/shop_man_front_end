package com.example.shopman.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.models.profile.getuserprofile.Address;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.getuserprofile.UserProfileMetadata;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileAddress;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileUser;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.MyPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final int MAX_IMAGES = 1; // Giới hạn 1 ảnh cho avatar

    private ImageView ivBack, ivProfileImage;
    private EditText etEmail, etName, etPhoneNumber, etAddress, etPincode, etCity, etCountry;
    private Button btnSave;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ApiManager apiManager;
    private String accessToken;
    private String avatar;
    private BroadcastReceiver logoutReceiver;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private List<Uri> selectedImageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_profile);

        // Áp dụng padding động cho navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            scrollView = findViewById(R.id.scrollView);
            scrollView.setPadding(0, 0, 0, navigationBarHeight);
            return insets;
        });

        // Khởi tạo views
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
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Đăng ký BroadcastReceiver để xử lý đăng xuất
        registerLogoutReceiver();

        // Khởi tạo ActivityResultLauncher để chọn ảnh
        initImagePickerLauncher();

        // Khởi tạo ActivityResultLauncher để yêu cầu quyền
        initRequestPermissionLauncher();

        // Lấy accessToken
        accessToken = MyPreferences.getString(this, KEY_ACCESS_TOKEN, null);
        if (TextUtils.isEmpty(accessToken)) {
            handleUnauthorized();
            return;
        }

        // Lấy thông tin hồ sơ
        fetchUserProfile();

        // Xử lý nút Back
        ivBack.setOnClickListener(v -> finish());

        // Xử lý nút Save
        btnSave.setOnClickListener(v -> saveProfile());

        // Xử lý nhấn vào ảnh đại diện
        ivProfileImage.setOnClickListener(v -> checkStoragePermission());
    }

    private void initImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                handleImageSelection(data);
            } else {
                Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Image picker failed: resultCode=" + result.getResultCode() + ", data=" + result.getData());
            }
        });
    }

    private void initRequestPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openImagePicker();
                Log.d(TAG, "Permission granted, opening image picker");
            } else {
                Toast.makeText(this, "Permission denied. Cannot access gallery.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Permission denied");
            }
        });
    }

    private void checkStoragePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // Chỉ cho phép chọn 1 ảnh
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleImageSelection(Intent data) {
        Log.d(TAG, "Handling image selection: data=" + data + ", selectedImageUris size before=" + selectedImageUris.size());
        selectedImageUris.clear(); // Xóa danh sách cũ vì chỉ cần 1 ảnh
        if (data.getClipData() != null) {
            int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGES - selectedImageUris.size());
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                if (selectedImageUris.size() < MAX_IMAGES) {
                    selectedImageUris.add(imageUri);
                }
                Log.d(TAG, "Added Uri: " + imageUri);
            }
        } else if (data.getData() != null && selectedImageUris.size() < MAX_IMAGES) {
            Uri imageUri = data.getData();
            selectedImageUris.add(imageUri);
            Log.d(TAG, "Added single Uri: " + imageUri);
        }
        if (selectedImageUris.size() >= MAX_IMAGES) {
            Toast.makeText(this, "Tối đa " + MAX_IMAGES + " ảnh", Toast.LENGTH_SHORT).show();
        }
        if (!selectedImageUris.isEmpty()) {
            Glide.with(this).load(selectedImageUris.get(0)).circleCrop().into(ivProfileImage);
            uploadImageToCloudinary(selectedImageUris.get(0));
        } else {
            Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        if (!isNetworkAvailable()) {
            showLoading(false);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        Log.d(TAG, "Starting upload for Uri: " + imageUri);
        String requestId = com.cloudinary.android.MediaManager.get().upload(imageUri)
                .unsigned("android_unsigned")
                .option("folder", "shopman/avatars")
                .option("public_id", "user_" + System.currentTimeMillis() + "_" + imageUri.getLastPathSegment())
                .callback(new com.cloudinary.android.callback.UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started for Uri: " + imageUri);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Upload progress for " + requestId + ": " + (bytes * 100f / totalBytes) + "%");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        showLoading(false);
                        String secureUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload success, URL: " + secureUrl);
                        if (secureUrl != null) {
                            avatar = secureUrl;
                            Glide.with(ProfileActivity.this).load(secureUrl).circleCrop().into(ivProfileImage);
                            Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "No secure URL returned in resultData: " + resultData);
                            Toast.makeText(ProfileActivity.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        showLoading(false);
                        Log.e(TAG, "Upload error: " + error.getDescription() + ", code: " + error.getCode());
                        Toast.makeText(ProfileActivity.this, "Failed to upload image: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, com.cloudinary.android.callback.ErrorInfo error) {
                        Log.w(TAG, "Upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
        Log.d(TAG, "Upload dispatched with requestId: " + requestId);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void registerLogoutReceiver() {
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received logout broadcast");
                handleUnauthorized();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.shopman.ACTION_LOGOUT");
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, filter);
    }

    private void fetchUserProfile() {
        if (!isNetworkAvailable()) {
            showLoading(false);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        apiManager.getUserProfile(accessToken, new ApiResponseListener<GetUserProfileResponse>() {
            @Override
            public void onSuccess(GetUserProfileResponse response) {
                showLoading(false);
                Log.d(TAG, "Raw response: " + response);
                if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                    Log.e(TAG, "Invalid profile response: response=" + response +
                            ", metadata=" + (response != null ? response.getMetadata() : null));
                    Toast.makeText(ProfileActivity.this, "Failed to load profile. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserProfileMetadata user = response.getMetadata().getMetadata();
                displayUserProfile(user);
                Log.d(TAG, "Successfully fetched user profile: " + user.getEmail());
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                Log.e(TAG, "Failed to fetch profile: " + errorMessage);
                if (errorMessage.contains("401")) {
                    handleUnauthorized();
                } else if (errorMessage.contains("404")) {
                    Toast.makeText(ProfileActivity.this, "Profile service unavailable. Contact support.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String pincode = etPincode.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(pincode) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPincode(pincode)) {
            Toast.makeText(this, "Pincode must be a valid number (4-10 digits)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            Toast.makeText(this, "Phone number must be 10-15 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileUser user = new UpdateProfileUser(name, phoneNumber, avatar != null ? avatar : "");
        UpdateProfileAddress addressObj = new UpdateProfileAddress("main", Integer.parseInt(pincode), address, city, country);
        UpdateProfileRequest request = new UpdateProfileRequest(user, addressObj);

        if (!isNetworkAvailable()) {
            showLoading(false);
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        btnSave.setEnabled(false);
        apiManager.updateUserProfile(accessToken, request, new ApiResponseListener<GetUserProfileResponse>() {
            @Override
            public void onSuccess(GetUserProfileResponse response) {
                showLoading(false);
                btnSave.setEnabled(true);
                Log.d(TAG, "Update response: " + response);
                if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                    Log.e(TAG, "Invalid update response: response=" + response +
                            ", metadata=" + (response != null ? response.getMetadata() : null));
                    Toast.makeText(ProfileActivity.this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserProfileMetadata user = response.getMetadata().getMetadata();
                displayUserProfile(user);
                Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully updated user profile: " + user.getEmail());

                // Gửi broadcast để thông báo MainActivity cập nhật avatar
                LocalBroadcastManager.getInstance(ProfileActivity.this).sendBroadcast(
                        new Intent("com.example.shopman.ACTION_UPDATE_USER")
                );

                // Quay lại MainActivity (không cần truyền avatar qua Intent vì đã lưu trong SharedPreferences)
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                showLoading(false);
                btnSave.setEnabled(true);
                Log.e(TAG, "Failed to update profile: " + errorMessage);
                if (errorMessage.contains("401")) {
                    handleUnauthorized();
                } else if (errorMessage.contains("404")) {
                    Toast.makeText(ProfileActivity.this, "Profile update service unavailable. Contact support.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUserProfile(UserProfileMetadata user) {
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etName.setText(user.getName() != null ? user.getName() : "");
        etPhoneNumber.setText(user.getPhone() != null ? user.getPhone() : "");
        avatar = user.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(this).load(avatar).circleCrop().into(ivProfileImage);
        } else {
            Glide.with(this).load(R.drawable.ic_menu_user).circleCrop().into(ivProfileImage); // Ảnh mặc định
        }

        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            Address address = user.getAddress().get(0);
            etPincode.setText(address.getPincode() > 0 ? String.valueOf(address.getPincode()) : "");
            etAddress.setText(address.getAddress() != null ? address.getAddress() : "");
            etCity.setText(address.getCity() != null ? address.getCity() : "");
            etCountry.setText(address.getCountry() != null ? address.getCountry() : "");
        }
    }

    private void handleUnauthorized() {
        MyPreferences.clear(this);
        Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private boolean isValidPincode(String pincode) {
        try {
            int value = Integer.parseInt(pincode);
            return value > 0 && pincode.length() >= 4 && pincode.length() <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10,15}");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
            logoutReceiver = null;
        }
    }
}