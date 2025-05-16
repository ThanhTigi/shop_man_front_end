package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopman.fragments.cart.CartFragment;
import com.example.shopman.fragments.home.HomeFragment;
import com.example.shopman.fragments.search.SearchFragment;
import com.example.shopman.fragments.setting.ProfileActivity;
import com.example.shopman.fragments.setting.SettingFragment;
import com.example.shopman.fragments.wishlist.WishlistFragment;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.AppConfig;
import com.example.shopman.utilitis.MyPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình thanh trạng thái: màu trắng, biểu tượng tối
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));

        setContentView(R.layout.activity_main);

        apiManager = new ApiManager(this);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        profileImageView = findViewById(R.id.ivProfile);

        // Lấy FCM token và gửi lên server
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String fcmToken = task.getResult();
                sendFcmTokenToServer(fcmToken);
            } else {
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
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(0);
            } else if (id == R.id.nav_wishlist) {
                viewPager.setCurrentItem(1);
            } else if (id == R.id.nav_cart) {
                viewPager.setCurrentItem(2);
            } else if (id == R.id.nav_search) {
                viewPager.setCurrentItem(3);
            } else if (id == R.id.nav_settings) {
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

    private void sendFcmTokenToServer(String fcmToken) {
        String accessToken = MyPreferences.getString(this, "access_token", "");
        apiManager.updateFcmToken(accessToken, fcmToken, new ApiResponseListener<Void>() {
            @Override
            public void onSuccess(Void response) {
                Log.d("MainActivity", "Cập nhật FCM token thành công");
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MainActivity", "Cập nhật FCM token thất bại: " + errorMessage);
            }
        });
    }

    public void switchToSearchWithData(String keywordSearch) {
        AppConfig.isSearch = true;
        AppConfig.keywordSearch = keywordSearch;
        viewPager.setCurrentItem(3, false);
    }
}