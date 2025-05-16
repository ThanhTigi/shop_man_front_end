package com.example.shopman.fragments.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;
import com.example.shopman.utilitis.ProductsConst;

import java.util.ArrayList;
import java.util.List;

public class DealActivity extends AppCompatActivity {

    private RecyclerView dealRecyclerView;
    private ProductAdapter dealAdapter;
    private List<Product> dealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        dealRecyclerView = findViewById(R.id.dealRecyclerView);

        // Khởi tạo danh sách Deal (lấy 6 sản phẩm làm ví dụ)
        dealList = new ArrayList<>();
        for (int i = 3; i < 9 && i < ProductsConst.totalProducts.size(); i++) {
            dealList.add(ProductsConst.totalProducts.get(i));
        }

        dealAdapter = new ProductAdapter(dealList);
        dealRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dealRecyclerView.setAdapter(dealAdapter);
    }
}