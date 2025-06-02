package com.example.shopman.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.R;
import com.example.shopman.models.CampaignResponse;
import com.example.shopman.models.Product;
import com.example.shopman.models.ProductResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.ArrayList;
import java.util.List;

public class CampaignActivity extends AppCompatActivity {

    private static final String TAG = "CampaignActivity";
    private static final int PAGE_SIZE = 10;

    private TextView tvCampaignTitle;
    private TextView tvCampaignDescription;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private int currentPage = 1; // Bắt đầu từ trang 1
    private int totalPages = 1; // Mặc định 1 để tránh tải thêm khi chưa có dữ liệu
    private boolean isLoading = false;
    private String campaignSlug;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        // Khởi tạo views
        tvCampaignTitle = findViewById(R.id.tvCampaignTitle);
        tvCampaignDescription = findViewById(R.id.tvCampaignDescription);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Lấy campaignSlug từ Intent
        campaignSlug = getIntent().getStringExtra("campaignSlug");
        if (campaignSlug == null || campaignSlug.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin campaign", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Khởi tạo danh sách sản phẩm và adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, "campaign"); // Sửa constructor

        // Cài đặt RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setAdapter(productAdapter);

        // Thêm sự kiện cuộn để hỗ trợ infinite scroll
        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoading && currentPage <= totalPages) { // Chỉ tải khi cuộn xuống
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount - 2) {
                        loadMoreProducts();
                    }
                }
            }
        });

        // Tải dữ liệu ban đầu
        loadCampaignDetails();
        loadProducts();
    }

    private void loadCampaignDetails() {
        apiManager.getCampaignDetails(campaignSlug, new ApiResponseListener<CampaignResponse>() {
            @Override
            public void onSuccess(CampaignResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    CampaignResponse.Campaign campaign = response.getMetadata().getMetadata().getCampaign();
                    tvCampaignTitle.setText(campaign.getTitle() != null ? campaign.getTitle() : "Chiến dịch");
                    tvCampaignDescription.setText(campaign.getDescription() != null ? campaign.getDescription() : "");
                } else {
                    Toast.makeText(CampaignActivity.this, "Không thể tải chi tiết campaign", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load campaign details: " + errorMessage);
                Toast.makeText(CampaignActivity.this, "Lỗi tải chi tiết campaign", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        if (isLoading) return; // Tránh gọi trùng lặp
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getCampaignProducts(campaignSlug, currentPage, PAGE_SIZE, new ApiResponseListener<ProductResponse>() {
            @Override
            public void onSuccess(ProductResponse response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<Product> newProducts = response.getMetadata().getMetadata().getProducts();
                    if (newProducts != null) {
                        if (currentPage == 1) {
                            productList.clear();
                        }
                        productList.addAll(newProducts);
                        productAdapter.notifyDataSetChanged();
                        totalPages = response.getMetadata().getMetadata().getTotalPages();
                        currentPage++;
                    } else {
                        Toast.makeText(CampaignActivity.this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CampaignActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load products: " + errorMessage);
                Toast.makeText(CampaignActivity.this, "Lỗi tải sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreProducts() {
        if (currentPage <= totalPages && !isLoading) {
            loadProducts();
        }
    }
}