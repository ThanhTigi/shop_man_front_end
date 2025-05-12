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
import com.example.shopman.utilitis.ProductsConst;

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

        switch (category.toLowerCase()) {
            case "beauty":
                products.add(ProductsConst.totalProducts.get(1));
                products.add(ProductsConst.totalProducts.get(2));
                break;
            case "fashion":
                products.add(ProductsConst.totalProducts.get(3));
                products.add(ProductsConst.totalProducts.get(4));
                break;
            case "kids":
                products.add(ProductsConst.totalProducts.get(5));
                products.add(ProductsConst.totalProducts.get(6));
                break;
            case "men":
                products.add(ProductsConst.totalProducts.get(7));
                products.add(ProductsConst.totalProducts.get(8));
                break;
            case "women":
                products.add(ProductsConst.totalProducts.get(9));
                products.add(ProductsConst.totalProducts.get(10));
                break;
            default:
                products.add(ProductsConst.totalProducts.get(11));
                products.add(ProductsConst.totalProducts.get(12));
                break;
        }
        return products;
    }
}