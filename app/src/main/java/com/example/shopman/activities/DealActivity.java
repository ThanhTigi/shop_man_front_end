package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.DealofTheDay.DealProduct;
import com.example.shopman.models.DealofTheDay.DealProductResponse;
import com.example.shopman.models.Product;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.ArrayList;
import java.util.List;

public class DealActivity extends AppCompatActivity {

    private static final String TAG = "DealActivity";
    private static final int PAGE_SIZE = 10;

    private RecyclerView dealRecyclerView;
    private ProductAdapter dealAdapter;
    private List<Product> dealList;
    private ProgressBar progressBar;
    private ApiManager apiManager;
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        // Khởi tạo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ưu đãi trong ngày");
        }

        // Khởi tạo views
        dealRecyclerView = findViewById(R.id.dealRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Cài đặt RecyclerView
        dealRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dealList = new ArrayList<>();
        dealAdapter = new ProductAdapter(this, dealList, "deal");
        dealRecyclerView.setAdapter(dealAdapter);

        // Thêm sự kiện cuộn cho infinite scroll
        dealRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoading && currentPage <= totalPages) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= dealList.size() - 2) {
                        loadDealProducts();
                    }
                }
            }
        });

        // Thêm sự kiện click cho sản phẩm
        dealAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(DealActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        // Tải dữ liệu ban đầu
        loadDealProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadDealProducts() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getDealProducts(currentPage, PAGE_SIZE, new ApiResponseListener<DealProductResponse>() {
            @Override
            public void onSuccess(DealProductResponse response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<DealProduct> dealProducts = response.getMetadata().getMetadata().getProducts();
                    totalPages = response.getMetadata().getMetadata().getTotalPages();
                    if (dealProducts != null && !dealProducts.isEmpty()) {
                        // Chuyển DealProduct thành Product
                        List<Product> newProducts = new ArrayList<>();
                        for (DealProduct dp : dealProducts) {
                            newProducts.add(dp.toProduct());
                        }
                        dealList.addAll(newProducts);
                        dealAdapter.notifyItemRangeInserted(dealList.size() - newProducts.size(), newProducts.size());
                        currentPage++;
                    } else {
                        Toast.makeText(DealActivity.this, "Không còn sản phẩm ưu đãi", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DealActivity.this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invalid response");
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DealActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error: " + errorMessage);
            }
        });
    }
}