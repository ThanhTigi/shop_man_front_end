package com.example.shopman.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.shopman.fragments.home.CategoryProductsFragment;
import com.example.shopman.R;

public class CategoryProductsActivity extends AppCompatActivity {
    private static final String TAG = "CategoryProductsActivity";
    private static final String EXTRA_SLUG = "extra_slug";
    private static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    private static final String EXTRA_CATEGORY_IMAGE = "extra_category_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình Window để tràn viền và hỗ trợ translucent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent)); // Nền trong suốt

        Log.d(TAG, "onCreate started");
        try {
            setContentView(R.layout.activity_category_products);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set layout: " + e.getMessage());
            Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Áp dụng padding động cho header sau khi layout được vẽ
        findViewById(android.R.id.content).post(() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

                // Padding cho header để tránh chồng lấn với status bar
                ConstraintLayout header = findViewById(R.id.header);
                if (header != null) {
                    header.setPadding(0, statusBarHeight, 0, 0); // PaddingTop động dựa trên statusBarHeight
                    Log.d(TAG, "Applied statusBarHeight: " + statusBarHeight); // Log chiều cao status bar
                    Log.d(TAG, "Header paddingTop: " + header.getPaddingTop()); // Log paddingTop thực tế
                    Log.d(TAG, "Header height: " + header.getHeight()); // Log chiều cao header sau khi vẽ
                }

                // Padding cho fragment_container
                FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
                if (fragmentContainer != null) {
                    fragmentContainer.setPadding(0, 0, 0, navigationBarHeight); // Padding dưới để tránh navigation bar
                    Log.d(TAG, "Applied navigationBarHeight: " + navigationBarHeight); // Log chiều cao navigation bar
                }

                return insets;
            });
            ViewCompat.requestApplyInsets(findViewById(android.R.id.content)); // Yêu cầu áp dụng insets ngay
        });

        ImageView ivBack = findViewById(R.id.ivBack);
        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        if (ivBack == null || tvCategoryTitle == null) {
            Log.e(TAG, "One or more views not found: ivBack=" + (ivBack == null) +
                    ", tvCategoryTitle=" + (tvCategoryTitle == null));
            Toast.makeText(this, "Layout initialization error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBack.setOnClickListener(v -> finish());

        String slug = getIntent().getStringExtra(EXTRA_SLUG);
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        String categoryImageUrl = getIntent().getStringExtra(EXTRA_CATEGORY_IMAGE);
        if (slug == null || slug.isEmpty()) {
            Log.e(TAG, "Slug is null or empty");
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvCategoryTitle.setText((categoryName != null ? categoryName : "Category") + " Products");
        Log.d(TAG, "tvCategoryTitle text set to: " + tvCategoryTitle.getText()); // Log text của TextView

        CategoryProductsFragment fragment = CategoryProductsFragment.newInstance(slug, categoryName, categoryImageUrl);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public static Intent createIntent(Context context, String slug, String categoryName, String categoryImageUrl) {
        Intent intent = new Intent(context, CategoryProductsActivity.class);
        intent.putExtra(EXTRA_SLUG, slug);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        intent.putExtra(EXTRA_CATEGORY_IMAGE, categoryImageUrl);
        return intent;
    }
}