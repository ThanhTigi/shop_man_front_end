package com.example.shopman.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.shopman.R;
import com.example.shopman.adapters.FullScreenImageAdapter;

import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {
    private static final String TAG = "FullScreenImageActivity";
    private ViewPager2 viewPager;
    private FullScreenImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_full_screen_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.viewPager), (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            v.setPadding(0, statusBarHeight, 0, navigationBarHeight); // Padding trên và dưới
            return insets;
        });
        viewPager = findViewById(R.id.viewPager);
        ImageButton btnClose = findViewById(R.id.btnClose);

        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("image_urls");
        int initialPosition = getIntent().getIntExtra("initial_position", 0);
        float scaleFactor = getIntent().getFloatExtra("scale_factor", 1.0f);

        if (imageUrls != null && !imageUrls.isEmpty()) {
            adapter = new FullScreenImageAdapter(this, imageUrls, initialPosition);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(initialPosition, false); // Đặt vị trí ban đầu

            // Xử lý nút đóng
            btnClose.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "ImageUrls is null or empty");
            Toast.makeText(this, "Không tìm thấy ảnh", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}