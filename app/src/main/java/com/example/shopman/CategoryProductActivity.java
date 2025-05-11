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

public class CategoryProductActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvProducts;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.categoryRecyclerView);

        // Set up back button
        ivBack.setOnClickListener(v -> finish());

        // Retrieve the category from Intent
        String category = getIntent().getStringExtra("categoryName");
        if (category == null) {
            category = "Unknown";
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
        }
        tvCategoryTitle.setText(category + " Products");

        // Set up RecyclerView with GridLayoutManager (2 columns)
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        List<Product> products = getSampleProducts(category);
        ProductAdapter adapter = new ProductAdapter(products);
        rvProducts.setAdapter(adapter);
    }

    // Sample method to get products based on category
    private List<Product> getSampleProducts(String category) {
        List<Product> products = new ArrayList<>();
        List<String> sizes = Arrays.asList("S", "M", "L");

        switch (category.toLowerCase()) {
            case "beauty":
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
                break;
            case "fashion":
                products.add(new Product(
                        "T-Shirt",
                        "Casual Cotton T-Shirt",
                        "$20",
                        R.drawable.ic_launcher_background,
                        4.0f,
                        sizes,
                        "A comfortable cotton t-shirt for everyday wear."
                ));
                products.add(new Product(
                        "Jeans",
                        "Slim Fit Jeans",
                        "$30",
                        R.drawable.ic_launcher_background,
                        4.3f,
                        sizes,
                        "Stylish slim fit jeans made from durable denim."
                ));
                break;
            case "kids":
                products.add(new Product(
                        "Toy Car",
                        "Remote Control Car",
                        "$5",
                        R.drawable.ic_launcher_background,
                        4.1f,
                        sizes,
                        "A fun remote control car for kids."
                ));
                products.add(new Product(
                        "Dress",
                        "Floral Kids Dress",
                        "$12",
                        R.drawable.ic_launcher_background,
                        4.4f,
                        sizes,
                        "A cute floral dress for young girls."
                ));
                break;
            case "men":
                products.add(new Product(
                        "Shirt",
                        "Formal Shirt",
                        "$25",
                        R.drawable.ic_launcher_background,
                        4.2f,
                        sizes,
                        "A formal shirt perfect for office wear."
                ));
                products.add(new Product(
                        "Watch",
                        "Analog Watch",
                        "$50",
                        R.drawable.ic_launcher_background,
                        4.7f,
                        sizes,
                        "A stylish analog watch for men."
                ));
                break;
            case "women":
                products.add(new Product(
                        "Dress",
                        "Evening Dress",
                        "$35",
                        R.drawable.ic_launcher_background,
                        4.6f,
                        sizes,
                        "An elegant evening dress for women."
                ));
                products.add(new Product(
                        "Handbag",
                        "Leather Handbag",
                        "$45",
                        R.drawable.ic_launcher_background,
                        4.5f,
                        sizes,
                        "A premium leather handbag for daily use."
                ));
                break;
            default:
                products.add(new Product(
                        "Unknown Product",
                        "Unknown Description",
                        "$0",
                        R.drawable.ic_launcher_background,
                        0.0f,
                        sizes,
                        "No description available."
                ));
                break;
        }
        return products;
    }
}