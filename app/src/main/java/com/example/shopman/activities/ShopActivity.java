package com.example.shopman.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shopman.R;

public class ShopActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        TextView shopTitle = findViewById(R.id.shopTitle);
        String shopSlug = getIntent().getStringExtra("shopSlug");

        if (shopSlug != null) {
            shopTitle.setText("Shop: " + shopSlug);
            // Tải chi tiết shop từ API: apiManager.getShopDetails(shopSlug, ...)
        }
    }
}