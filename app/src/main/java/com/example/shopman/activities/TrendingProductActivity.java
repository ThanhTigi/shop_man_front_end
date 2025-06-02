package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.TopTrendingProducts.TrendingProduct;
import com.example.shopman.models.TopTrendingProducts.TrendingProductResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.ArrayList;
import java.util.List;

public class TrendingProductActivity extends AppCompatActivity {

    private static final String TAG = "TrendingProductActivity";
    private static final int PAGE_SIZE = 10;

    private RecyclerView rvProducts;
    private ImageView ivBack;
    private ProgressBar progressBar;
    private ProductAdapter adapter;
    private List<Product> products;
    private ApiManager apiManager;
    private float nextCursor = 0f;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_product);

        // Khởi tạo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sản phẩm thịnh hành");
        }

        // Khởi tạo views
        ivBack = findViewById(R.id.ivBack);
        rvProducts = findViewById(R.id.categoryRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Cài đặt RecyclerView
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        products = new ArrayList<>();
        adapter = new ProductAdapter(this, products, "trending");
        rvProducts.setAdapter(adapter);

        // Thêm sự kiện click cho nút back
        ivBack.setOnClickListener(v -> finish());

        // Thêm sự kiện cuộn cho infinite scroll
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoading) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= products.size() - 2 && nextCursor > 0) {
                        loadTrendingProducts();
                    }
                }
            }
        });

        // Tải dữ liệu ban đầu
        loadTrendingProducts();

        // Thêm sự kiện click cho sản phẩm
        adapter.setOnProductClickListener(product -> {
            // Chuyển đến ProductDetailsActivity
            Intent intent = new Intent(TrendingProductActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTrendingProducts() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getTrendingProducts(nextCursor, PAGE_SIZE, new ApiResponseListener<TrendingProductResponse>() {
            @Override
            public void onSuccess(TrendingProductResponse response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<TrendingProduct> trendingProducts = response.getMetadata().getMetadata().getProducts();
                    nextCursor = response.getMetadata().getMetadata().getNextCursor();
                    if (trendingProducts != null && !trendingProducts.isEmpty()) {
                        // Chuyển TrendingProduct thành Product
                        List<Product> newProducts = new ArrayList<>();
                        for (TrendingProduct tp : trendingProducts) {
                            newProducts.add(tp.toProduct());
                        }
                        products.addAll(newProducts);
                        adapter.notifyItemRangeInserted(products.size() - newProducts.size(), newProducts.size());
                    } else {
                        Toast.makeText(TrendingProductActivity.this, "Không còn sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TrendingProductActivity.this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invalid response");
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TrendingProductActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error: " + errorMessage);
            }
        });
    }
}