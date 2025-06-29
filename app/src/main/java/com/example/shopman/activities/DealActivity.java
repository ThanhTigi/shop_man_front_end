package com.example.shopman.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.DealVerticalProductAdapter;
import com.example.shopman.models.DealofTheDay.DealProductResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.SpacesItemDecoration;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DealActivity extends AppCompatActivity {

    private static final String TAG = "DealActivity";
    private static final int LIMIT = 29; // Số sản phẩm mỗi lần load
    private static final int ITEM_SPACING = 0;
    private static final int SPAN_COUNT = 2;
    private static final int LOAD_MORE_OFFSET = 100; // Offset để kích hoạt load more (100px từ cuối)

    private ImageView ivBack;
    private RecyclerView productRecyclerView;
    private DealVerticalProductAdapter productAdapter;
    private List<SearchProduct> productList;
    private ProgressBar productProgressBar;
    private ProgressBar loadMoreProgress;
    private TextView emptyView;
    private Object nextCursor = null; // Cursor cho load more
    private Float minPrice = null;
    private Float maxPrice = null;
    private Integer minRating = null;
    private String sortBy = null;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String previousSortBy = null;
    private ApiManager apiManager;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình window
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        Log.d(TAG, "onCreate started at " + getCurrentTime());
        try {
            setContentView(R.layout.activity_deal);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set layout: " + e.getMessage());
            Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý insets
        findViewById(android.R.id.content).post(() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                int navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;

                ConstraintLayout header = findViewById(R.id.header);
                if (header != null) {
                    header.setPadding(0, statusBarHeight, 0, 0);
                    Log.d(TAG, "Applied statusBarHeight: " + statusBarHeight);
                }

                if (productRecyclerView != null) {
                    productRecyclerView.setPadding(0, 0, 0, navigationBarHeight);
                    Log.d(TAG, "Applied navigationBarHeight: " + navigationBarHeight);
                }

                return insets;
            });
            ViewCompat.requestApplyInsets(findViewById(android.R.id.content));
        });

        // Khởi tạo view
        ivBack = findViewById(R.id.ivBack);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productProgressBar = findViewById(R.id.productProgressBar);
        loadMoreProgress = findViewById(R.id.loadMoreProgress);
        emptyView = findViewById(R.id.emptyView);

        if (ivBack == null || productRecyclerView == null || productProgressBar == null ||
                loadMoreProgress == null || emptyView == null) {
            Log.e(TAG, "One or more views not found");
            Toast.makeText(this, "Layout initialization error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo chip
        Chip chipDefault = findViewById(R.id.chipDefault);
        Chip chipPopularity = findViewById(R.id.chipPopularity);
        Chip chipPriceAsc = findViewById(R.id.chipPriceAsc);
        Chip chipPriceDesc = findViewById(R.id.chipPriceDesc);
        Chip chipFilter = findViewById(R.id.chipFilter);

        apiManager = new ApiManager(this);

        productList = new ArrayList<>();
        productAdapter = new DealVerticalProductAdapter(this, productList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));
        productRecyclerView.addItemDecoration(new SpacesItemDecoration(ITEM_SPACING));
        productRecyclerView.setAdapter(productAdapter);

        // Lắng nghe scroll từ NestedScrollView
        androidx.core.widget.NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new androidx.core.widget.NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY && !isLoading && !isLastPage) {
                        View contentView = nestedScrollView.getChildAt(0);
                        if (contentView != null && nestedScrollView.getHeight() + scrollY >= contentView.getHeight() - LOAD_MORE_OFFSET) {
                            Log.d(TAG, "Scroll to bottom detected, triggering load more with nextCursor: " +
                                    (nextCursor != null ? gson.toJson(nextCursor) : "null") + ", sortBy: " + sortBy);
                            loadMoreProducts();
                        }
                    }
                }
            });
        }

        Log.d(TAG, "Loading initial products with nextCursor: " + (nextCursor != null ? gson.toJson(nextCursor) : "null") +
                ", sortBy: " + sortBy);
        loadProducts();

        // Sự kiện click
        ivBack.setOnClickListener(v -> onBackPressed());

        chipDefault.setOnClickListener(v -> {
            sortBy = null;
            resetAndLoadProducts();
        });
        chipPopularity.setOnClickListener(v -> {
            sortBy = "{\"field\":\"sale_count\",\"order\":\"desc\"}";
            resetAndLoadProducts();
        });
        chipPriceAsc.setOnClickListener(v -> {
            sortBy = "{\"field\":\"price\",\"order\":\"asc\"}";
            resetAndLoadProducts();
        });
        chipPriceDesc.setOnClickListener(v -> {
            sortBy = "{\"field\":\"price\",\"order\":\"desc\"}";
            resetAndLoadProducts();
        });


        chipFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void loadProducts() {
        if (!isLoading) {
            isLoading = true;
            productProgressBar.setVisibility(View.VISIBLE);
            productList.clear();
            productAdapter.notifyDataSetChanged();
            emptyView.setVisibility(View.GONE);
            productRecyclerView.setVisibility(View.VISIBLE);

            Log.d(TAG, "Calling API: Loading initial products with lastSortValues: " + (nextCursor != null ? gson.toJson(nextCursor) : "null") +
                    ", sortBy: " + sortBy + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice +
                    ", minRating: " + minRating + ", limit: " + LIMIT);
            apiManager.getDealOfTheDay(nextCursor, LIMIT, minPrice, maxPrice, minRating, sortBy, new ApiResponseListener<DealProductResponse>() {
                @Override
                public void onSuccess(DealProductResponse response) {
                    isLoading = false;
                    productProgressBar.setVisibility(View.GONE);
                    handleResponse(response);
                    Log.d(TAG, "API Success: Loaded initial products, lastSortValues: " + (nextCursor != null ? gson.toJson(nextCursor) : "null"));
                }

                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    productProgressBar.setVisibility(View.GONE);
                    Log.e(TAG, "API Error: Failed to load deals: " + errorMessage);
                    Toast.makeText(DealActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    emptyView.setVisibility(View.VISIBLE);
                    productRecyclerView.setVisibility(View.GONE);
                }
            });
        } else {
            Log.w(TAG, "Load skipped: isLoading = true");
        }
    }

    private void loadMoreProducts() {
        if (!isLoading && !isLastPage && nextCursor != null) {
            isLoading = true;
            loadMoreProgress.setVisibility(View.VISIBLE);
            Log.d(TAG, "Calling API: Loading more products with lastSortValues: " + (nextCursor != null ? gson.toJson(nextCursor) : "null") +
                    ", sortBy: " + sortBy + ", minPrice: " + minPrice + ", maxPrice: " + maxPrice +
                    ", minRating: " + minRating);

            if (shouldResetOnSortChange()) {
                nextCursor = null;
                isLastPage = false;
                loadProducts();
                return;
            }

            apiManager.getDealOfTheDay(nextCursor, LIMIT, minPrice, maxPrice, minRating, sortBy, new ApiResponseListener<DealProductResponse>() {
                @Override
                public void onSuccess(DealProductResponse response) {
                    isLoading = false;
                    loadMoreProgress.setVisibility(View.GONE);
                    handleResponse(response);
                    Log.d(TAG, "API Success: Loaded more products, lastSortValues: " + (nextCursor != null ? gson.toJson(nextCursor) : "null"));
                }

                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    loadMoreProgress.setVisibility(View.GONE);
                    Log.e(TAG, "API Error: Failed to load more deals: " + errorMessage);
                    Toast.makeText(DealActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w(TAG, "Load more skipped: isLoading=" + isLoading + ", isLastPage=" + isLastPage + ", nextCursor=" + nextCursor);
        }
    }
    private void handleResponse(DealProductResponse response) {
        if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
            List<SearchProduct> products = response.getMetadata().getMetadata().getData();
            Object cursorObj = response.getMetadata().getMetadata().getLastSortValues();
            nextCursor = parseNextCursor(cursorObj);

            if (productList.isEmpty()) {
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            } else {
                int startPosition = productList.size();
                productList.addAll(products);
                productAdapter.notifyItemRangeInserted(startPosition, products.size());
            }

            Log.d(TAG, "Handle Response: Loaded " + products.size() + " deal products, updated nextCursor: " +
                    (nextCursor != null ? gson.toJson(nextCursor) : "null"));

            if (nextCursor == null || products.isEmpty()) {
                isLastPage = true;
                Log.w(TAG, "No more products or nextCursor is null, marking as last page");
            }
        } else {
            Log.w(TAG, "No deal products available in response");
            emptyView.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.GONE);
            isLastPage = true;
            nextCursor = null;
        }
    }

    private Object parseNextCursor(Object cursorObj) {
        if (cursorObj == null) return null;
        if (cursorObj instanceof List) {
            List<?> list = (List<?>) cursorObj;
            if (list.size() == 2) {
                Object score = list.get(0); // Có thể là Float hoặc Double
                Object id = list.get(1);    // Có thể là String
                if (score instanceof Number && id instanceof String) {
                    return cursorObj; // Trả về mảng gốc nếu hợp lệ
                }
            }
        }
        Log.w(TAG, "Invalid cursor format, defaulting to null: " + cursorObj);
        return null;
    }

    private boolean shouldResetOnSortChange() {
        if (previousSortBy == null) {
            previousSortBy = sortBy;
            return false;
        }
        boolean shouldReset = !previousSortBy.equals(sortBy) || (minPrice != null || maxPrice != null || minRating != null);
        if (shouldReset) {
            previousSortBy = sortBy;
        }
        return shouldReset;
    }

    private void resetAndLoadProducts() {
        nextCursor = null;
        isLastPage = false;
        Log.d(TAG, "Resetting and reloading products with new sort/filter, nextCursor: " + nextCursor + ", sortBy: " + sortBy);
        loadProducts();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme); // Áp dụng theme
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        // Price Range Slider
        RangeSlider rangeSliderPrice = dialogView.findViewById(R.id.rangeSliderPrice);
        TextView tvMinPrice = dialogView.findViewById(R.id.tvMinPrice);
        TextView tvMaxPrice = dialogView.findViewById(R.id.tvMaxPrice);

        if (rangeSliderPrice == null || tvMinPrice == null || tvMaxPrice == null) {
            Log.e(TAG, "One or more views in dialog_filter not found");
            return;
        }

        rangeSliderPrice.setValueFrom(0f);
        rangeSliderPrice.setValueTo(10000000f); // 10,000,000 VND
        float defaultMin = minPrice != null ? minPrice : 0f;
        float defaultMax = maxPrice != null ? maxPrice : 10000000f;
        rangeSliderPrice.setValues(defaultMin, defaultMax);
        updatePriceText(tvMinPrice, tvMaxPrice, defaultMin, defaultMax);

        rangeSliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            updatePriceText(tvMinPrice, tvMaxPrice, values.get(0), values.get(1));
        });

        // Rating Slider
        Slider sliderRating = dialogView.findViewById(R.id.sliderRating);
        TextView tvMinRating = dialogView.findViewById(R.id.tvMinRating);

        if (sliderRating == null || tvMinRating == null) {
            Log.e(TAG, "One or more views in dialog_filter not found");
            return;
        }

        sliderRating.setValueFrom(0f);
        sliderRating.setValueTo(5f);
        float defaultRating = minRating != null ? minRating : 0f;
        sliderRating.setValue(defaultRating);
        tvMinRating.setText(String.format("Min Rating: %.1f", defaultRating));

        sliderRating.addOnChangeListener((slider, value, fromUser) -> {
            tvMinRating.setText(String.format("Min Rating: %.1f", value));
        });

        // Buttons
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnApply = dialogView.findViewById(R.id.btnApply);

        if (btnCancel == null || btnApply == null) {
            Log.e(TAG, "Buttons in dialog_filter not found");
            return;
        }

        btnCancel.setOnClickListener(v -> {
            AlertDialog dialog = (AlertDialog) v.getTag();
            if (dialog != null) dialog.dismiss();
        });

        btnApply.setOnClickListener(v -> {
            List<Float> priceValues = rangeSliderPrice.getValues();
            minPrice = priceValues.get(0);
            maxPrice = priceValues.get(1);
            minRating = (int) Math.round(sliderRating.getValue());

            resetAndLoadProducts();

            AlertDialog dialog = (AlertDialog) v.getTag();
            if (dialog != null) dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        btnCancel.setTag(dialog);
        btnApply.setTag(dialog);
        dialog.show();
    }
    private void updatePriceText(TextView tvMin, TextView tvMax, float min, float max) {
        tvMin.setText(String.format("Min: %.0f VND", min));
        tvMax.setText(String.format("Max: %.0f VND", max));
    }

    private String encodeCursor(Object cursor) {
        if (cursor == null) return null;
        try {
            return URLEncoder.encode(gson.toJson(cursor), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to encode cursor: " + e.getMessage());
            return null;
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}