package com.example.shopman.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.adapters.DiscountAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Discount;
import com.example.shopman.models.Shop.FollowShopResponse;
import com.example.shopman.models.Shop.ShopProductsResponse;
import com.example.shopman.models.Shop.ShopResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.MyPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class ShopDetailActivity extends AppCompatActivity implements ApiResponseListener {
    public static final String EXTRA_SHOP_SLUG = "extra_shop_slug";
    private static final String TAG = "ShopDetailActivity";

    private ApiManager apiManager;
    private ShopResponse shopInfo;
    private List<Discount> discounts;
    private List<com.example.shopman.models.Product> products;
    private boolean isLoading = false;
    private List<Object> lastSortValues;
    private ProductAdapter productAdapter;
    private DiscountAdapter discountAdapter;
    private boolean isShopFollowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cấu hình Window để tràn viền
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat insetsController = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent, null));

        setContentView(R.layout.activity_shop_detail);

        // Áp dụng padding động cho header để ảnh thumbnail tràn lên status bar
        findViewById(android.R.id.content).post(() -> {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                ConstraintLayout header = findViewById(R.id.header);
                if (header != null) {
                    header.setPadding(0, -statusBarHeight, 0, 0); // Loại bỏ padding trên để ảnh tràn lên
                }
                return insets;
            });
            ViewCompat.requestApplyInsets(findViewById(android.R.id.content));
        });

        apiManager = new ApiManager(this);
        String shopSlug = getIntent().getStringExtra(EXTRA_SHOP_SLUG);
        Log.d(TAG, "Received shopSlug from Intent: " + shopSlug);
        if (TextUtils.isEmpty(shopSlug)) {
            Log.e(TAG, "Shop slug is null or empty");
            Toast.makeText(this, "Shop not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        showLoading(true);
        apiManager.getShopDetails(shopSlug, this);
    }

    private void showLoading(boolean isLoading) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onSuccess(Object response) {
        showLoading(false);
        if (response instanceof ShopResponse) {
            shopInfo = (ShopResponse) response;
            isShopFollowed = shopInfo.getMetadata().getMetadata().getIsFollowing();
            updateShopInfoUI();
            loadDiscounts();
            loadProducts();
        } else if (response instanceof ShopProductsResponse) {
            ShopProductsResponse shopProducts = (ShopProductsResponse) response;
            if (products == null) products = new ArrayList<>();
            products.addAll(shopProducts.getMetadata().getMetadata().getData().stream()
                    .map(SearchProduct::toProduct)
                    .collect(Collectors.toList()));
            lastSortValues = shopProducts.getMetadata().getMetadata().getLastSortValues();
            setupProductRecyclerView();
        }
    }

    @Override
    public void onError(String errorMessage) {
        showLoading(false);
        Log.e(TAG, "API Error: " + errorMessage);
        Toast.makeText(this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
        // Khi có lỗi (như HTTP 500), đặt lastSortValues thành null để dừng gọi API
        lastSortValues = null;
    }

    private void updateShopInfoUI() {
        if (shopInfo == null || shopInfo.getMetadata() == null || shopInfo.getMetadata().getMetadata() == null) {
            Log.e(TAG, "Shop info is invalid");
            Toast.makeText(this, "Dữ liệu shop không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView tvShopName = findViewById(R.id.tvShopName);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ImageView ivShopLogo = findViewById(R.id.ivShopLogo);
        ImageView ivShopThumb = findViewById(R.id.ivShopThumb);
        Button btnFollow = findViewById(R.id.btnFollow);

        ShopResponse.Shop shop = shopInfo.getMetadata().getMetadata().getShop();
        tvShopName.setText(shop.getName());
        ratingBar.setRating(Float.parseFloat(shop.getRating())); // Lấy rating từ API, không fix cứng
        Glide.with(this).load(shop.getLogo()).placeholder(R.drawable.ic_placeholder_thumb).into(ivShopLogo);
        Glide.with(this).load(shop.getThumb()).placeholder(R.drawable.ic_placeholder_thumb).into(ivShopThumb);
        updateFollowButton(btnFollow);
    }

    private void loadDiscounts() {
        if (shopInfo == null || shopInfo.getMetadata() == null || shopInfo.getMetadata().getMetadata() == null) {
            Log.e(TAG, "Shop info or discounts unavailable");
            return;
        }

        discounts = shopInfo.getMetadata().getMetadata().getDiscounts();
        RecyclerView discountRecyclerView = findViewById(R.id.discountRecyclerView);
        if (discountRecyclerView != null) {
            discountRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            discountAdapter = new DiscountAdapter(this, discounts);
            discountAdapter.setOnDiscountClickListener(this::showDiscountDialog);
            discountRecyclerView.setAdapter(discountAdapter);
        }
    }

    private void loadProducts() {
        if (shopInfo == null || shopInfo.getMetadata() == null || shopInfo.getMetadata().getMetadata() == null) {
            Log.e(TAG, "Shop info unavailable for loading products");
            return;
        }

        String shopSlug = shopInfo.getMetadata().getMetadata().getShop().getSlug();
        apiManager.getShopProducts(shopSlug, null, this);
    }

    private void setupProductRecyclerView() {
        RecyclerView productRecyclerView = findViewById(R.id.productRecyclerView);
        if (productRecyclerView == null) {
            Log.e(TAG, "Product RecyclerView not found");
            return;
        }

        // Sử dụng GridLayoutManager với 2 cột
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, products, "shop");
        productAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });
        productRecyclerView.setAdapter(productAdapter);

        productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1) {
                    if (lastSortValues != null && !lastSortValues.isEmpty()) {
                        isLoading = true;
                        showLoading(true);
                        apiManager.getShopProducts(shopInfo.getMetadata().getMetadata().getShop().getSlug(), lastSortValues, new ApiResponseListener<ShopProductsResponse>() {
                            @Override
                            public void onSuccess(ShopProductsResponse response) {
                                isLoading = false;
                                showLoading(false);
                                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                                    List<SearchProduct> newData = response.getMetadata().getMetadata().getData();
                                    if (newData != null && !newData.isEmpty()) {
                                        products.addAll(newData.stream()
                                                .map(SearchProduct::toProduct)
                                                .collect(Collectors.toList()));
                                        lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                                        productAdapter.notifyDataSetChanged();
                                    } else {
                                        // Nếu không còn dữ liệu mới, đặt lastSortValues thành null
                                        lastSortValues = null;
                                    }
                                } else {
                                    lastSortValues = null; // Không có metadata, dừng gọi
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                isLoading = false;
                                showLoading(false);
                                Toast.makeText(ShopDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                lastSortValues = null; // Lỗi xảy ra (như HTTP 500), dừng gọi
                            }
                        });
                    } else {
                        Log.d(TAG, "No more data to load, lastSortValues is null or empty");
                    }
                }
            }
        });
    }

    private void updateFollowButton(Button btnFollow) {
        if (btnFollow == null) {
            Log.e(TAG, "Follow button not found");
            return;
        }

        btnFollow.setText(isShopFollowed ? "Unfollow" : "Follow");
        btnFollow.setSelected(isShopFollowed);
        // Thay đổi backgroundTint dựa trên trạng thái
        if (isShopFollowed) {
            btnFollow.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray, null)); // Màu xám khi unfollow
        } else {
            btnFollow.setBackgroundTintList(getResources().getColorStateList(R.color.red, null)); // Màu đỏ khi follow
        }
        btnFollow.setTextColor(getResources().getColor(android.R.color.white, null));
        btnFollow.setOnClickListener(v -> toggleFollowShop());
    }

    private void toggleFollowShop() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (TextUtils.isEmpty(accessToken)) {
            Toast.makeText(this, "Vui lòng đăng nhập để theo dõi shop", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class)); // Giả định có LoginActivity
            return;
        }

        if (shopInfo == null || shopInfo.getMetadata() == null || shopInfo.getMetadata().getMetadata() == null) {
            Log.e(TAG, "Shop info unavailable for toggle follow");
            Toast.makeText(this, "Dữ liệu shop không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        String shopId = String.valueOf(shopInfo.getMetadata().getMetadata().getShop().getId());
        ApiResponseListener<FollowShopResponse> listener = new ApiResponseListener<FollowShopResponse>() {
            @Override
            public void onSuccess(FollowShopResponse response) {
                if (response != null && response.getStatus() == 200) {
                    isShopFollowed = !isShopFollowed;
                    shopInfo.getMetadata().getMetadata().setIsFollowing(isShopFollowed);
                    updateFollowButton(findViewById(R.id.btnFollow));
                    Toast.makeText(ShopDetailActivity.this, isShopFollowed ? "Đã theo dõi shop" : "Đã hủy theo dõi shop", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopDetailActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Toggle follow shop error: " + errorMessage);
                Toast.makeText(ShopDetailActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

        if (isShopFollowed) {
            apiManager.unfollowShop(shopId, listener);
        } else {
            apiManager.followShop(shopId, listener);
        }
    }

    private void showDiscountDialog(Discount discount) {
        if (discount == null) {
            Log.e(TAG, "Discount is null, cannot show dialog");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_discount_detail, null);
        builder.setView(dialogView);

        TextView tvDiscountName = dialogView.findViewById(R.id.tvDiscountName);
        TextView tvDiscountDesc = dialogView.findViewById(R.id.tvDiscountDesc);
        TextView tvDiscountValue = dialogView.findViewById(R.id.tvDiscountValue);
        TextView tvDiscountCode = dialogView.findViewById(R.id.tvDiscountCode);
        TextView tvDiscountPeriod = dialogView.findViewById(R.id.tvDiscountPeriod);

        tvDiscountName.setText(discount.getName());
        tvDiscountDesc.setText(discount.getDescription());
        tvDiscountValue.setText(discount.getValue() + (discount.getType().equals("percent") ? "%" : "đ"));
        tvDiscountCode.setText(discount.getCode());
        tvDiscountPeriod.setText(String.format("%s - %s",
                formatDate(discount.getStartDate()),
                formatDate(discount.getEndDate())));

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
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

    public static Intent createIntent(Context context, String shopSlug) {
        Intent intent = new Intent(context, ShopDetailActivity.class);
        intent.putExtra(EXTRA_SHOP_SLUG, shopSlug);
        return intent;
    }
}