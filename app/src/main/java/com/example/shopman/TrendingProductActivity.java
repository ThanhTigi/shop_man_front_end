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

    private List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();
        products.add(ProductsConst.totalProducts.get(11));
        products.add(ProductsConst.totalProducts.get(12));
        products.add(ProductsConst.totalProducts.get(7));
        products.add(ProductsConst.totalProducts.get(5));
        products.add(ProductsConst.totalProducts.get(9));
        products.add(ProductsConst.totalProducts.get(3));
        products.add(ProductsConst.totalProducts.get(1));
        products.add(ProductsConst.totalProducts.get(2));


        return products;
    }
}