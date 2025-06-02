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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình thanh trạng thái: màu trắng, biểu tượng tối
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
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
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                sendFcmTokenToServer(fcmToken);
            } else {
                Log.e(TAG, "Failed to get FCM token: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Toast.makeText(MainActivity.this, "Không lấy được FCM token", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nhấn vào ảnh hồ sơ
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
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
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0);
            } else if (itemId == R.id.nav_wishlist) {
                viewPager.setCurrentItem(1);
            } else if (itemId == R.id.nav_cart) {
                viewPager.setCurrentItem(2);
            } else if (itemId == R.id.nav_search) {
                viewPager.setCurrentItem(3);
            } else if (itemId == R.id.nav_settings) {
                viewPager.setCurrentItem(4);
            }
            return true;
        });

        // Đồng bộ ViewPager2 với BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
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

    private void registerLogoutReceiver() {
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received logout broadcast");
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        };
        IntentFilter filter = new IntentFilter("com.example.shopman.ACTION_LOGOUT");
        registerReceiver(logoutReceiver, filter);
    }

    private void sendFcmTokenToServer(String fcmToken) {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (TextUtils.isEmpty(accessToken)) {
            Log.w(TAG, "No access token available for FCM token update");
            return;
        }
        apiManager.updateFcmToken(accessToken, fcmToken, new ApiResponseListener<Void>() {
            @Override
            public void onSuccess(Void response) {
                Log.d(TAG, "Successfully updated FCM token");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to update FCM token: " + errorMessage);
                Toast.makeText(MainActivity.this, "Không thể cập nhật FCM token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void switchToSearchWithData(String keywordSearch) {
        AppConfig.isSearch = true;
        AppConfig.keywordSearch = keywordSearch;
        viewPager.setCurrentItem(3, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            unregisterReceiver(logoutReceiver);
            logoutReceiver = null;
        }
    }
}