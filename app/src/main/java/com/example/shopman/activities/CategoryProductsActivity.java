package com.example.shopman.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
        Log.d(TAG, "onCreate started");
        try {
            setContentView(R.layout.activity_category_products);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set layout: " + e.getMessage());
            Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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