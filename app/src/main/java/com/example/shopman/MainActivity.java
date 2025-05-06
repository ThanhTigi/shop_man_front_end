package com.example.shopman;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Set up the ViewPager2 with a FragmentStateAdapter
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
                return 5; // Total number of fragments
            }
        };

        viewPager.setAdapter(pagerAdapter);

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


        // Sync BottomNavigationView when the page changes
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
}
