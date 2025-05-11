package com.example.shopman;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrendingProductActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_product);

        ivBack = findViewById(R.id.ivBack);
        rvProducts = findViewById(R.id.categoryRecyclerView);

        ivBack.setOnClickListener(v -> finish());

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Product> products = getSampleProducts();
        ProductAdapter adapter = new ProductAdapter(products);
        rvProducts.setAdapter(adapter);
    }

    // Sample method to get products based on category
    private List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();
        List<String> sizes = Arrays.asList("S", "M", "L");

        products.add(new Product(
                "Lipstick",
                "Matte Red Lipstick",
                "$10",
                R.drawable.ic_launcher_background,
                4.5f,
                sizes,
                "This is a long-lasting matte red lipstick suitable for all occasions."
        ));
        products.add(new Product(
                "Foundation",
                "Full Coverage Foundation",
                "$15",
                R.drawable.ic_launcher_background,
                4.2f,
                sizes,
                "A full coverage foundation with a natural finish."
        ));

        return products;
    }
}