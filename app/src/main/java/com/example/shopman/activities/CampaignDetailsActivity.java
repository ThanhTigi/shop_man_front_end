package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.DiscountAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Campaign.CampaignProductsResponse;
import com.example.shopman.models.Campaign.CampaignResponse;
import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CampaignDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_CAMPAIGN_SLUG = "extra_campaign_slug";
    private static final String TAG = "CampaignDetailsActivity";
    private static final int LIMIT = 10;
    private String lastId = null;
    private RecyclerView discountRecyclerView, productRecyclerView;
    private DiscountAdapter discountAdapter;
    private ProductAdapter productAdapter;
    private String campaignSlug;
    private NestedScrollView nestedScrollView;
    private View productProgressBar;
    private boolean isLoadingProducts = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign);

        campaignSlug = getIntent().getStringExtra(EXTRA_CAMPAIGN_SLUG);
        Log.d(TAG, "Received campaignSlug from Intent: " + campaignSlug);
        if (campaignSlug == null) {
            Toast.makeText(this, "Invalid campaign slug", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadCampaignDetails();
        loadCampaignProducts();
    }

    private void initializeViews() {
        nestedScrollView = findViewById(R.id.nestedScrollView);
        discountRecyclerView = findViewById(R.id.discountRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productProgressBar = findViewById(R.id.productProgressBar);

        // Cấu hình RecyclerView cho Discount (horizontal)
        discountRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        discountAdapter = new DiscountAdapter(this, new ArrayList<>());
        discountRecyclerView.setAdapter(discountAdapter);
        discountAdapter.setOnDiscountClickListener(this::showDiscountDialog);

        // Cấu hình RecyclerView cho Product (grid 2 cột)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(this, new ArrayList<>(), "campaign");
        productRecyclerView.setAdapter(productAdapter);
        productAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(CampaignDetailsActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        // Thêm listener cho nút back
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "ivBack not found in layout");
        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY && !isLoadingProducts && !isLastPage) {
                    View contentView = nestedScrollView.getChildAt(0);
                    if (contentView != null && nestedScrollView.getHeight() + scrollY >= contentView.getHeight() - 100) {
                        loadMoreProducts();
                    }
                }
            }
        });
    }

    private void showDiscountDialog(Discount discount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_discount_detail, null);
        builder.setView(dialogView);

        TextView tvDiscountName = dialogView.findViewById(R.id.tvDiscountName);
        TextView tvDiscountDesc = dialogView.findViewById(R.id.tvDiscountDesc);
        TextView tvDiscountValue = dialogView.findViewById(R.id.tvDiscountValue);
        TextView tvDiscountCode = dialogView.findViewById(R.id.tvDiscountCode);
        TextView tvDiscountPeriod = dialogView.findViewById(R.id.tvDiscountPeriod);

        tvDiscountName.setText(discount.getName());
        tvDiscountDesc.setText(discount.getDescription() != null ? discount.getDescription() : "Không có mô tả");
        tvDiscountValue.setText(discount.getValue() + (discount.getType().equals("percent") ? "%" : "đ"));
        tvDiscountCode.setText(discount.getCode() != null ? discount.getCode() : "Không có mã");
        tvDiscountPeriod.setText("Từ " + formatDate(discount.getStartDate()) + " đến " + formatDate(discount.getEndDate()));

        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void loadCampaignDetails() {
        new ApiManager(this).getCampaignDetails(campaignSlug, new ApiResponseListener<CampaignResponse>() {
            @Override
            public void onSuccess(CampaignResponse response) {
                findViewById(R.id.ivCampaignBanner).setVisibility(View.VISIBLE);
                // Cập nhật banner từ API (bỏ comment nếu dùng Glide)
                // Glide.with(CampaignDetailsActivity.this).load(response.getMetadata().getMetadata().getCampaign().getThumb()).into(findViewById(R.id.ivCampaignBanner));

                findViewById(R.id.tvCampaignTitleInfo).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tvCampaignTitleInfo)).setText(response.getMetadata().getMetadata().getCampaign().getTitle());

                findViewById(R.id.tvCampaignDescription).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.tvCampaignDescription)).setText(response.getMetadata().getMetadata().getCampaign().getDescription());

                findViewById(R.id.tvCampaignDate).setVisibility(View.VISIBLE);
                String startTime = formatDate(response.getMetadata().getMetadata().getCampaign().getStartTime());
                String endTime = formatDate(response.getMetadata().getMetadata().getCampaign().getEndTime());
                ((TextView) findViewById(R.id.tvCampaignDate)).setText("Bắt đầu: " + startTime + " | Kết thúc: " + endTime);

                discountAdapter = new DiscountAdapter(CampaignDetailsActivity.this, response.getMetadata().getMetadata().getDiscounts());
                discountAdapter.setOnDiscountClickListener(CampaignDetailsActivity.this::showDiscountDialog);
                discountRecyclerView.setAdapter(discountAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading campaign details: " + errorMessage);
                Toast.makeText(CampaignDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDate);

            SimpleDateFormat vietnamFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
            vietnamFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
            return date != null ? vietnamFormat.format(date) : isoDate;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return isoDate;
        }
    }

    private void loadCampaignProducts() {
        if (isLoadingProducts) return;
        isLoadingProducts = true;
        productProgressBar.setVisibility(View.VISIBLE);

        new ApiManager(this).getCampaignProducts(campaignSlug, LIMIT, lastId, new ApiResponseListener<CampaignProductsResponse>() {
            @Override
            public void onSuccess(CampaignProductsResponse response) {
                isLoadingProducts = false;
                productProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<com.example.shopman.models.Product> products = response.getMetadata().getMetadata().getProducts();
                    if (products != null && !products.isEmpty()) {
                        lastId = products.get(products.size() - 1).getId();
                        productAdapter.addProducts(products);
                        isLastPage = products.size() < LIMIT;
                    } else {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoadingProducts = false;
                productProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading campaign products: " + errorMessage);
                Toast.makeText(CampaignDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreProducts() {
        loadCampaignProducts();
    }
}