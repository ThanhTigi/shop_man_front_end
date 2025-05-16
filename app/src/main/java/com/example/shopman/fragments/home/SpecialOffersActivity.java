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

public class SpecialOffersActivity extends AppCompatActivity {

    private RecyclerView specialOffersRecyclerView;
    private ProductAdapter specialOffersAdapter;
    private List<Product> specialOffersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_offers);

        specialOffersRecyclerView = findViewById(R.id.specialOffersRecyclerView);

        // Khởi tạo danh sách Special Offers (lấy 6 sản phẩm làm ví dụ)
        specialOffersList = new ArrayList<>();
        for (int i = 6; i < 12 && i < ProductsConst.totalProducts.size(); i++) {
            specialOffersList.add(ProductsConst.totalProducts.get(i));
        }

        specialOffersAdapter = new ProductAdapter(specialOffersList);
        specialOffersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        specialOffersRecyclerView.setAdapter(specialOffersAdapter);
    }
}