package com.example.shopman.fragments.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;

import java.util.ArrayList;
import java.util.List;

public class NewArrivalsActivity extends AppCompatActivity {

    private RecyclerView newArrivalsRecyclerView;
    private ProductAdapter newArrivalsAdapter;
    private List<Product> newArrivalsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_arrivals);

        newArrivalsRecyclerView = findViewById(R.id.newArrivalsRecyclerView);

        // Khởi tạo danh sách New Arrivals (lấy 6 sản phẩm làm ví dụ)
        newArrivalsList = new ArrayList<>();


        newArrivalsAdapter = new ProductAdapter(newArrivalsList);
        newArrivalsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        newArrivalsRecyclerView.setAdapter(newArrivalsAdapter);
    }
}