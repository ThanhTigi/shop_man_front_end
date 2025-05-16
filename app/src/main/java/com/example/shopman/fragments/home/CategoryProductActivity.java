package com.example.shopman.fragments.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvProducts;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        ivBack = findViewById(R.id.ivBack);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.categoryRecyclerView);

        ivBack.setOnClickListener(v -> finish());

        String category = getIntent().getStringExtra("categoryName");
        if (category == null) {
            category = "Unknown";
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
        }
        tvCategoryTitle.setText(category + " Products");

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Product> products = getSampleProducts(category);
        ProductAdapter adapter = new ProductAdapter(products);
        rvProducts.setAdapter(adapter);
    }

    private List<Product> getSampleProducts(String category) {
        List<Product> products = new ArrayList<>();

        return products;
    }
}