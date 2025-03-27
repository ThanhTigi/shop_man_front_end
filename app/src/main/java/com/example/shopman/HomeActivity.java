package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivVoiceSearch;
    private Button sortButton, filterButton;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerDots;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private RecyclerView dealRecyclerView, trendingRecyclerView;
    private ProductAdapter dealAdapter, trendingAdapter;
    private List<Product> dealList, trendingList;
    private TextView dealViewAll, trendingViewAll, newArrivalsViewAll;
    private Button visitNowButton;
    private BottomNavigationView bottomNavigation;
    private ImageView ivMenu, ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        etSearch = findViewById(R.id.etSearch);
        ivVoiceSearch = findViewById(R.id.ivVoiceSearch);
        sortButton = findViewById(R.id.sortButton);
        filterButton = findViewById(R.id.filterButton);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        bannerViewPager = findViewById(R.id.bannerViewPager);
        bannerDots = findViewById(R.id.bannerDots);
        dealRecyclerView = findViewById(R.id.dealRecyclerView);
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        dealViewAll = findViewById(R.id.dealViewAll);
        trendingViewAll = findViewById(R.id.trendingViewAll);
        newArrivalsViewAll = findViewById(R.id.newArrivalsViewAll);
        visitNowButton = findViewById(R.id.visitNowButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ivMenu = findViewById(R.id.ivMenu);
        ivProfile = findViewById(R.id.ivProfile);

        // Set up toolbar click listeners
        ivMenu.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show());
        ivProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });

        // Set up search bar
        ivVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Voice Search clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Sort and Filter buttons
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Sort button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Filter button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up RecyclerView for categories
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Beauty", R.drawable.ic_beauty));
        categoryList.add(new Category("Fashion", R.drawable.ic_fashion));
        categoryList.add(new Category("Kids", R.drawable.ic_kids));
        categoryList.add(new Category("Men", R.drawable.ic_mens));
        categoryList.add(new Category("Women", R.drawable.ic_womens));

        categoryAdapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up ViewPager2 for banners
        bannerList = new ArrayList<>();
        bannerList.add(new Banner(R.drawable.banner_image));
        bannerList.add(new Banner(R.drawable.banner_image));
        bannerList.add(new Banner(R.drawable.banner_image));

        bannerAdapter = new BannerAdapter(bannerList, () -> {
            Toast.makeText(HomeActivity.this, "Shop Now clicked", Toast.LENGTH_SHORT).show();
        });
        bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(bannerDots, bannerViewPager, (tab, position) -> {
        }).attach();

        // Set up RecyclerView for Deal of the Day
        dealList = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        sizes.add("6 UK");
        sizes.add("7 UK");
        sizes.add("8 UK");
        sizes.add("9 UK");
        sizes.add("10 UK");
        dealList.add(new Product("Women Printed Kurta", "Neque porro quisquam est qui", "₹1500", R.drawable.deal_image_1, 4.5f, sizes, "A beautiful printed kurta for women, perfect for casual wear."));
        dealList.add(new Product("HRX by Hrithik Roshan", "Neque porro quisquam est qui", "₹2499", R.drawable.deal_image_2, 4.0f, sizes, "A stylish and comfortable outfit by HRX, designed for active lifestyles."));

        dealAdapter = new ProductAdapter(dealList);
        dealRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dealRecyclerView.setAdapter(dealAdapter);

        dealViewAll.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Deal View All clicked", Toast.LENGTH_SHORT).show());

        // Set up Special Offers
        visitNowButton.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Visit Now clicked", Toast.LENGTH_SHORT).show());

        // Set up RecyclerView for Trending Products
        trendingList = new ArrayList<>();
        trendingList.add(new Product("IMC Smartwatch", "SHH 2019 2014 44mm", "₹2500", R.drawable.trending_image_1, 4.2f, sizes, "A smartwatch with advanced features for fitness tracking."));
        trendingList.add(new Product("Labbin White", "For Men and Female", "₹1500", R.drawable.trending_image_2, 3.8f, sizes, "A versatile white shirt suitable for both men and women."));

        trendingAdapter = new ProductAdapter(trendingList);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(trendingAdapter);

        trendingViewAll.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "Trending View All clicked", Toast.LENGTH_SHORT).show());

        newArrivalsViewAll.setOnClickListener(v -> Toast.makeText(HomeActivity.this, "New Arrivals View All clicked", Toast.LENGTH_SHORT).show());

        // Inside onCreate, in the bottomNavigation.setOnNavigationItemSelectedListener
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(HomeActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_wishlist) {
                Intent wishlistIntent = new Intent(HomeActivity.this, WishlistActivity.class);
                startActivity(wishlistIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_cart) {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_search) {
                Toast.makeText(HomeActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Toast.makeText(HomeActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
}