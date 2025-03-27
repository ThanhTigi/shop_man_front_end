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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivVoiceSearch;
    private TextView itemCount;
    private Button sortButton, filterButton;
    private RecyclerView wishlistRecyclerView;
    private ProductAdapter wishlistAdapter;
    private List<Product> wishlistItems;
    private BottomNavigationView bottomNavigation;
    private ImageView ivMenu, ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Initialize views
        etSearch = findViewById(R.id.etSearch);
        ivVoiceSearch = findViewById(R.id.ivVoiceSearch);
        itemCount = findViewById(R.id.itemCount);
        sortButton = findViewById(R.id.sortButton);
        filterButton = findViewById(R.id.filterButton);
        wishlistRecyclerView = findViewById(R.id.wishlistRecyclerView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        ivMenu = findViewById(R.id.ivMenu);
        ivProfile = findViewById(R.id.ivProfile);

        // Set up toolbar click listeners
        ivMenu.setOnClickListener(v -> Toast.makeText(WishlistActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show());
        ivProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(WishlistActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        });

        // Set up search bar
        ivVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WishlistActivity.this, "Voice Search clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Sort and Filter buttons
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WishlistActivity.this, "Sort button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WishlistActivity.this, "Filter button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up RecyclerView for wishlist items
        wishlistItems = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        sizes.add("6 UK");
        sizes.add("7 UK");
        sizes.add("8 UK");
        sizes.add("9 UK");
        sizes.add("10 UK");
        wishlistItems.add(new Product("Black Winter Jacket", "Autumn And Winter Casual Cotton padded jacket", "₹499", R.drawable.trending_image_1, 4.0f, sizes, "A warm and stylish jacket for winter."));
        wishlistItems.add(new Product("Mens Starry Shirt", "100% Cotton Fabric", "₹999", R.drawable.trending_image_2, 4.5f, sizes, "A starry-patterned shirt made of 100% cotton."));
        wishlistItems.add(new Product("Black Dress", "Solid Black Dress For Women, Sexy Chain Shorts Ladi...", "₹2000", R.drawable.trending_image_1, 4.0f, sizes, "A chic black dress with chain shorts for women."));
        wishlistItems.add(new Product("Pink Embroidered Dress", "Earthen Rose Pink Embroidered Tiered Max...", "₹1900", R.drawable.trending_image_2, 4.5f, sizes, "A beautiful pink embroidered tiered maxi dress."));
        wishlistItems.add(new Product("Flare Dress", "Anthaea Black & Rust Orange Floral Print Tiered Midi F...", "₹1990", R.drawable.deal_image_1, 4.0f, sizes, "A floral print midi dress with a flared design."));
        wishlistItems.add(new Product("Denim Dress", "Casual Denim Dress", "₹1500", R.drawable.deal_image_2, 4.0f, sizes, "A casual denim dress for everyday wear."));

        wishlistAdapter = new ProductAdapter(wishlistItems);
        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        // Update item count
        itemCount.setText(wishlistItems.size() + " Items");

        // Set up Bottom Navigation
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                finish(); // Go back to HomeActivity
                return true;
            } else if (item.getItemId() == R.id.nav_wishlist) {
                Toast.makeText(WishlistActivity.this, "Wishlist already open", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_cart) {
                Toast.makeText(WishlistActivity.this, "Cart clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_search) {
                Toast.makeText(WishlistActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Toast.makeText(WishlistActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_wishlist);
    }
}