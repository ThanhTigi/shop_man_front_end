package com.example.shopman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.shopman.activities.LoginActivity;
import com.example.shopman.fragments.cart.CartFragment;
import com.example.shopman.fragments.home.HomeFragment;
import com.example.shopman.fragments.search.SearchFragment;
import com.example.shopman.activities.ProfileActivity;
import com.example.shopman.fragments.setting.SettingFragment;
import com.example.shopman.fragments.wishlist.WishlistFragment;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.remote.RetrofitClient;
import com.example.shopman.utilitis.AppConfig;
import com.example.shopman.utilitis.MyPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView profileImageView;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ApiManager apiManager;
    private BroadcastReceiver logoutReceiver;
    private boolean isChangingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra token trước khi khởi tạo UI
        if (!checkTokens()) {
            redirectToLogin("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
            return;
        }

        // Cấu hình System UI
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_DEFAULT);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));

        setContentView(R.layout.activity_main);

        // Khởi tạo RetrofitClient
        RetrofitClient.init(this);

        apiManager = new ApiManager(this);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        profileImageView = findViewById(R.id.ivProfile);

        // Đăng ký BroadcastReceiver để xử lý đăng xuất
        registerLogoutReceiver();

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
    }

    private boolean checkTokens() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        String refreshToken = MyPreferences.getString(this, "refresh_token", null);
        boolean isValid = !TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(refreshToken);
        Log.d(TAG, "Token check: access_token=" + accessToken + ", refresh_token=" + refreshToken + ", valid=" + isValid);
        return isValid;
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

    private void registerLogoutReceiver() {
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received logout broadcast");
                redirectToLogin("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
            }
        };
        IntentFilter filter = new IntentFilter("com.example.shopman.ACTION_LOGOUT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(logoutReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(logoutReceiver, filter);
        }
        Log.d(TAG, "Registered logout receiver with RECEIVER_NOT_EXPORTED");
    }

    private void sendFcmTokenToServer(String fcmToken) {
        if (!checkTokens()) {
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
            unregisterReceiver(logoutReceiver);
            logoutReceiver = null;
            Log.d(TAG, "Unregistered logout receiver");
        }
        isChangingActivity = true; // Ngăn tương tác sau khi hủy
    }
}