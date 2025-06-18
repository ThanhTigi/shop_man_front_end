package com.example.shopman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.shopman.activities.LoginActivity;
import com.example.shopman.activities.ProfileActivity;
import com.example.shopman.fragments.cart.CartFragment;
import com.example.shopman.fragments.home.HomeFragment;
import com.example.shopman.fragments.search.SearchFragment;
import com.example.shopman.fragments.setting.SettingFragment;
import com.example.shopman.fragments.wishlist.WishlistFragment;
import com.example.shopman.models.login.User;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.remote.RetrofitClient;
import com.example.shopman.utilitis.AppConfig;
import com.example.shopman.utilitis.MyPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView profileImageView;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ApiManager apiManager;
    private BroadcastReceiver logoutReceiver;
    private BroadcastReceiver userUpdateReceiver;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra và khôi phục token trước khi khởi tạo UI
        if (!restoreSession()) {
            Log.d(TAG, "Failed to restore session, redirecting to Login");
            redirectToLogin("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        // Cấu hình System UI (tràn viền và xử lý padding)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // Tràn viền
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE); // Ẩn thanh khi vuốt
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent)); // Nền trong suốt

        setContentView(R.layout.activity_main);

        // Áp dụng padding động cho toàn bộ layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

            // Áp padding cho toolbar
            LinearLayout toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setPadding(0, statusBarHeight, 0, 0); // Padding trên cho status bar
            }

            // Áp padding cho bottom navigation
            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
            if (bottomNav != null) {
                bottomNav.setPadding(0, 0, 0, navigationBarHeight); // Padding dưới cho navigation bar
            }

            // Trả về insets để tiếp tục xử lý
            return insets;
        });

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        apiManager = new ApiManager(this);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        profileImageView = findViewById(R.id.ivProfile);

        if (viewPager == null || bottomNavigationView == null || profileImageView == null) {
            Log.e(TAG, "One or more views (viewPager, bottomNavigation, profileImageView) not found in layout");
            return;
        }

        // Đăng ký BroadcastReceiver
        registerBroadcastReceivers();

        // Lấy FCM token và gửi lên server
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!isFinishing() && task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                Log.d(TAG, "FCM token retrieved: " + fcmToken);
                sendFcmTokenToServer(fcmToken);
            } else {
                Log.e(TAG, "Failed to get FCM token: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, "Không lấy được FCM token", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý sự kiện nhấn vào ảnh hồ sơ
        profileImageView.setOnClickListener(v -> {
            if (!isChangingActivity) {
                Log.d(TAG, "Navigating to ProfileActivity");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Thiết lập adapter cho ViewPager2
        FragmentStateAdapter pagerAdapter = new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new HomeFragment();
                    case 1:
                        return new WishlistFragment();
                    case 2:
                        return new CartFragment();
                    case 3:
                        return new SearchFragment();
                    case 4:
                        return new SettingFragment();
                    default:
                        return new HomeFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 5;
            }
        };

        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false); // Vô hiệu hóa cuộn ngang

        // Xử lý sự kiện chọn mục trên BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (isChangingActivity) return false;
            int itemId = item.getItemId();
            Log.d(TAG, "BottomNavigation selected: " + itemId);
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0, false);
            } else if (itemId == R.id.nav_wishlist) {
                viewPager.setCurrentItem(1, false);
            } else if (itemId == R.id.nav_cart) {
                viewPager.setCurrentItem(2, false);
            } else if (itemId == R.id.nav_search) {
                viewPager.setCurrentItem(3, false);
            } else if (itemId == R.id.nav_settings) {
                viewPager.setCurrentItem(4, false);
            }
            return true;
        });

        // Đồng bộ ViewPager2 với BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (isChangingActivity) return;
                super.onPageSelected(position);
                Log.d(TAG, "ViewPager page selected: " + position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_wishlist);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_cart);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.nav_search);
                        break;
                    case 4:
                        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
                        break;
                }
            }
        });

        // Cập nhật ảnh đại diện khi khởi động
        updateProfileImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật ảnh đại diện khi quay lại
        updateProfileImage();
    }

    private void updateProfileImage() {
        if (isFinishing() || isChangingActivity) return;

        // Lấy avatar từ Intent (fallback)
        String avatarUrl = getIntent().getStringExtra("user_avatar");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Log.d(TAG, "Updating profile image from Intent: " + avatarUrl);
            loadImage(avatarUrl);
            return;
        }

        // Lấy avatar từ SharedPreferences
        String userJson = MyPreferences.getString(this, "current_user_meta_data", null);
        if (userJson != null && !userJson.isEmpty()) {
            try {
                User user = new Gson().fromJson(userJson, User.class);
                String avatar = user.getAvatar();
                if (avatar != null && !avatar.isEmpty()) {
                    Log.d(TAG, "Updating profile image from SharedPreferences: " + avatar);
                    loadImage(avatar);
                } else {
                    Log.w(TAG, "No avatar found in SharedPreferences");
                    loadImage(null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse user JSON: " + e.getMessage());
                loadImage(null);
            }
        } else {
            Log.w(TAG, "No user data found in SharedPreferences");
            loadImage(null);
        }
    }

    private void loadImage(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this).load(avatarUrl).circleCrop()
                    .placeholder(R.drawable.ic_menu_user)
                    .error(R.drawable.ic_menu_user)
                    .into(profileImageView);
        } else {
            Glide.with(this).load(R.drawable.ic_menu_user).circleCrop().into(profileImageView);
        }
    }

    private boolean restoreSession() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        String refreshToken = MyPreferences.getString(this, "refresh_token", null);
        Log.d(TAG, "Restoring session - access_token: " + accessToken + ", refresh_token: " + refreshToken);

        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(refreshToken)) {
            Log.w(TAG, "No valid tokens found in preferences");
            return false;
        }
        return true;
    }

    private void redirectToLogin(String message) {
        if (!isFinishing() && !isChangingActivity) {
            isChangingActivity = true;
            Log.d(TAG, "Redirecting to LoginActivity: " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void registerBroadcastReceivers() {
        // Đăng ký logout receiver
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received logout broadcast");
                loadImage(null); // Xóa avatar
                redirectToLogin("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                logoutReceiver, new IntentFilter("com.example.shopman.ACTION_LOGOUT")
        );

        // Đăng ký user update receiver
        userUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received user update broadcast");
                updateProfileImage();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(
                userUpdateReceiver, new IntentFilter("com.example.shopman.ACTION_UPDATE_USER")
        );
        Log.d(TAG, "Registered broadcast receivers with LocalBroadcastManager");
    }

    private void sendFcmTokenToServer(String fcmToken) {
        if (!restoreSession()) {
            Log.w(TAG, "Invalid tokens, skipping FCM token update");
            redirectToLogin("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }
        String accessToken = MyPreferences.getString(this, "access_token", null);
        Log.d(TAG, "Sending FCM token to server: " + fcmToken);
        apiManager.updateFcmToken(accessToken, fcmToken, new ApiResponseListener<Void>() {
            @Override
            public void onSuccess(Void response) {
                Log.d(TAG, "Successfully updated FCM token");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to update FCM token: " + errorMessage);
                if (!isFinishing()) {
                    Toast.makeText(MainActivity.this, "Không thể cập nhật FCM token: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void switchToSearchWithData(String keywordSearch) {
        if (!isChangingActivity) {
            Log.d(TAG, "Switching to SearchFragment with keyword: " + keywordSearch);
            AppConfig.isSearch = true;
            AppConfig.keywordSearch = keywordSearch;
            viewPager.setCurrentItem(3, false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
            logoutReceiver = null;
        }
        if (userUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(userUpdateReceiver);
            userUpdateReceiver = null;
        }
        Log.d(TAG, "Unregistered broadcast receivers");
        isChangingActivity = true; // Ngăn tương tác sau khi hủy
    }
}