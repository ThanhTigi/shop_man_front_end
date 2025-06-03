package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.DiscountAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Campaign.CampaignProductsResponse;
import com.example.shopman.models.Campaign.CampaignResponse;
import com.example.shopman.models.Product;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.ArrayList;
import java.util.List;

public class CampaignActivity extends AppCompatActivity {
    private ApiManager apiManager;
    private String campaignSlug;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private TextView campaignTitle, campaignDescription;
    private RecyclerView discountsRecyclerView;
    private DiscountAdapter discountAdapter;
    private List<CampaignResponse.Discount> discountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        // Lấy campaignSlug từ Intent
        campaignSlug = getIntent().getStringExtra("campaignSlug");
        if (campaignSlug == null) {
            Toast.makeText(this, "Không tìm thấy campaign", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiManager = new ApiManager(this);
        productList = new ArrayList<>();
        discountList = new ArrayList<>();

        // Khởi tạo views
        campaignTitle = findViewById(R.id.campaignTitle);
        campaignDescription = findViewById(R.id.campaignDescription);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        discountsRecyclerView = findViewById(R.id.discountsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo ProductAdapter với displayType = "campaign"
        productAdapter = new ProductAdapter(this, productList, "campaign");
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        // Khởi tạo DiscountAdapter
        discountAdapter = new DiscountAdapter(new ArrayList<>(discountList));
        discountsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discountsRecyclerView.setAdapter(discountAdapter);

        // Load dữ liệu
        loadCampaignDetails();
        loadCampaignProducts(1, 10);
    }

    private void loadCampaignDetails() {
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getCampaignDetails(campaignSlug, new ApiResponseListener<CampaignResponse>() {
            @Override
            public void onSuccess(CampaignResponse response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null &&
                        response.getMetadata().getMetadata() != null) {
                    CampaignResponse.Campaign campaign = response.getMetadata().getMetadata().getCampaign();
                    campaignTitle.setText(campaign.getTitle());
                    campaignDescription.setText(campaign.getDescription());
                    discountList.clear();
                    discountList.addAll(response.getMetadata().getMetadata().getDiscount());
                    discountAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CampaignActivity.this, "Dữ liệu campaign không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CampaignActivity.this, "Lỗi tải chi tiết campaign: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCampaignProducts(int page, int limit) {
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getCampaignProducts(campaignSlug, page, limit, new ApiResponseListener<CampaignProductsResponse>() {
            @Override
            public void onSuccess(CampaignProductsResponse response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null &&
                        response.getMetadata().getMetadata() != null) {
                    productList.addAll(response.getMetadata().getMetadata().getProducts());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CampaignActivity.this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CampaignActivity.this, "Lỗi tải sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}