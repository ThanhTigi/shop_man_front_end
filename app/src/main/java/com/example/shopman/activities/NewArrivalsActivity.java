package com.example.shopman.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.NewArrivals.NewArrivalProduct;
import com.example.shopman.models.NewArrivals.NewArrivalsResponse;
import com.example.shopman.models.Product;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class NewArrivalsActivity extends AppCompatActivity {
    private static final String TAG = "NewArrivalsActivity";
    private static final int PAGE_SIZE = 10;

    private TextView tvTitle;
    private RecyclerView newArrivalsRecyclerView;
    private ImageView ivBack;
    private ProductAdapter newArrivalsAdapter;
    private List<Product> newArrivalsList;
    private ApiManager apiManager;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_new_arrivals);

        tvTitle = findViewById(R.id.tvTitle);
        newArrivalsRecyclerView = findViewById(R.id.newArrivalsRecyclerView);
        ivBack = findViewById(R.id.ivBack);

        tvTitle.setText("New Arrivals");
        ivBack.setOnClickListener(v -> finish());

        // Khởi tạo RecyclerView và ApiManager
        newArrivalsList = new ArrayList<>();
        newArrivalsAdapter = new ProductAdapter(this, newArrivalsList, "new_arrivals");
        newArrivalsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        newArrivalsRecyclerView.setAdapter(newArrivalsAdapter);

        apiManager = new ApiManager(this);

        // Thêm sự kiện click cho sản phẩm
        newArrivalsAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(NewArrivalsActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        // Thêm OnScrollListener để xử lý infinite scroll
        newArrivalsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Chỉ load khi cuộn xuống
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                            loadMoreProducts();
                        }
                    }
                }
            }
        });

        // Tải sản phẩm
        loadProducts(currentPage, PAGE_SIZE, true);
    }

    private void loadProducts(int page, Integer pageSize, boolean isNewSearch) {
        if (isLoading || isLastPage) return;
        isLoading = true;

        if (isNewSearch) {
            newArrivalsList.clear();
            newArrivalsAdapter.notifyDataSetChanged();
            currentPage = 1;
            isLastPage = false;
        }

        apiManager.getNewArrivals(page, pageSize, new ApiResponseListener<NewArrivalsResponse>() {
            @Override
            public void onSuccess(NewArrivalsResponse response) {
                isLoading = false;
                Log.d(TAG, "Products loaded successfully: " + new Gson().toJson(response));

                if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                    Toast.makeText(NewArrivalsActivity.this, "Dữ liệu không hợp lệ", Toast.LENGTH_LONG).show();
                    return;
                }

                List<NewArrivalProduct> newArrivalProducts = response.getMetadata().getMetadata().getProducts();
                int totalPages = response.getMetadata().getMetadata().getTotalPages();

                if (newArrivalProducts == null || newArrivalProducts.isEmpty()) {
                    Toast.makeText(NewArrivalsActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    isLastPage = true;
                    return;
                }

                // Ánh xạ NewArrivalProduct sang Product
                List<Product> products = new ArrayList<>();
                for (NewArrivalProduct nap : newArrivalProducts) {
                    products.add(nap.toProduct());
                }

                newArrivalsList.addAll(products);
                newArrivalsAdapter.notifyItemRangeInserted(newArrivalsList.size() - products.size(), products.size());

                if (page >= totalPages) {
                    isLastPage = true;
                } else {
                    currentPage++;
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                Log.e(TAG, "Failed to load products: " + errorMessage);
                Toast.makeText(NewArrivalsActivity.this, "Lỗi tải sản phẩm: " + errorMessage, Toast.LENGTH_LONG).show();
                if (errorMessage.contains("Session expired")) {
                    Toast.makeText(NewArrivalsActivity.this, "Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadMoreProducts() {
        Log.d(TAG, "Loading more products for page: " + currentPage);
        loadProducts(currentPage, PAGE_SIZE, false);
    }
}