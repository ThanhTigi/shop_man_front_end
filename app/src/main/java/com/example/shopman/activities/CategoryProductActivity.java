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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.category.CategoryProductResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductActivity extends AppCompatActivity {
    private static final String TAG = "CategoryProductActivity";
    private static final String EXTRA_SLUG = "extra_slug";
    private static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    private static final int PAGE_SIZE = 10;

    private TextView tvCategoryTitle;
    private RecyclerView rvProducts;
    private ImageView ivBack;
    private ProductAdapter adapter;
    private List<Product> productList; // Thay SearchProduct bằng Product
    private ApiManager apiManager;
    private String slug;
    private String lastSortValues;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        ivBack = findViewById(R.id.ivBack);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.categoryRecyclerView);

        ivBack.setOnClickListener(v -> finish());

        // Lấy slug và categoryName từ Intent
        slug = getIntent().getStringExtra(EXTRA_SLUG);
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        if (slug == null) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvCategoryTitle.setText((categoryName != null ? categoryName : "Category") + " Products");

        // Khởi tạo RecyclerView và ApiManager
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, "category");
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);

        apiManager = new ApiManager(this);

        // Thêm sự kiện click cho sản phẩm
        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(CategoryProductActivity.this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        // Thêm OnScrollListener để xử lý infinite scroll
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        loadProducts(null, PAGE_SIZE, true);
    }

    private void loadProducts(String lastSortValues, Integer pageSize, boolean isNewSearch) {
        if (isLoading || isLastPage) return;
        isLoading = true;

        if (isNewSearch) {
            productList.clear();
            adapter.notifyDataSetChanged();
            this.lastSortValues = null;
            isLastPage = false;
        }

        String lastSortValuesJson = (lastSortValues != null && !lastSortValues.isEmpty()) ? lastSortValues : null;
        apiManager.getCategoryProducts(slug, lastSortValuesJson, pageSize, new ApiResponseListener<CategoryProductResponse>() {
            @Override
            public void onSuccess(CategoryProductResponse response) {
                isLoading = false;
                Log.d(TAG, "Products loaded successfully: " + new Gson().toJson(response));

                if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                    Toast.makeText(CategoryProductActivity.this, "Dữ liệu không hợp lệ", Toast.LENGTH_LONG).show();
                    return;
                }

                List<SearchProduct> categoryProducts = response.getMetadata().getMetadata().getData();
                List<Object> sortValues = response.getMetadata().getMetadata().getLastSortValues();

                if (sortValues != null && !sortValues.isEmpty()) {
                    CategoryProductActivity.this.lastSortValues = new Gson().toJson(sortValues);
                }

                if (categoryProducts == null || categoryProducts.isEmpty()) {
                    Toast.makeText(CategoryProductActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    isLastPage = true;
                    return;
                }

                // Ánh xạ SearchProduct sang Product
                List<Product> products = new ArrayList<>();
                for (SearchProduct sp : categoryProducts) {
                    products.add(sp.toProduct());
                }

                productList.addAll(products);
                adapter.notifyItemRangeInserted(productList.size() - products.size(), products.size());

                if (products.size() < (pageSize != null ? pageSize : PAGE_SIZE)) {
                    isLastPage = true;
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                Log.e(TAG, "Failed to load products: " + errorMessage);
                Toast.makeText(CategoryProductActivity.this, "Lỗi tải sản phẩm: " + errorMessage, Toast.LENGTH_LONG).show();
                if (errorMessage.contains("Session expired")) {
                    Toast.makeText(CategoryProductActivity.this, "Vui lòng đăng nhập lại!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadMoreProducts() {
        Log.d(TAG, "Loading more products with lastSortValues: " + lastSortValues);
        loadProducts(lastSortValues, PAGE_SIZE, false);
    }

    public static Intent createIntent(Context context, String slug, String categoryName) {
        Intent intent = new Intent(context, CategoryProductActivity.class);
        intent.putExtra(EXTRA_SLUG, slug);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        return intent;
    }
}