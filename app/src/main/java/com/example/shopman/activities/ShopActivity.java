package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shopman.adapters.DiscountAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.Shop.ShopProductsResponse;
import com.example.shopman.models.ShopResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private ApiManager apiManager;
    private String shopSlug;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private TextView shopName, shopDescription;
    private ImageView shopLogo;
    private RecyclerView discountsRecyclerView;
    private DiscountAdapter discountAdapter;
    private List<ShopResponse.Discount> discountList;
    private List<Object> lastSortValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Lấy shopSlug từ Intent
        shopSlug = getIntent().getStringExtra("shopSlug");
        if (shopSlug == null) {
            Toast.makeText(this, "Không tìm thấy shop", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiManager = new ApiManager(this);
        productList = new ArrayList<>();
        discountList = new ArrayList<>();
        lastSortValues = null;

        // Initialize views
        shopName = findViewById(R.id.shopName);
        shopDescription = findViewById(R.id.shopDescription);
        shopLogo = findViewById(R.id.shopLogo);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        discountsRecyclerView = findViewById(R.id.discountsRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo ProductAdapter với displayType = "shop"
        productAdapter = new ProductAdapter(this, productList, "shop");
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        // Khởi tạo DiscountAdapter với List<Object>
        discountAdapter = new DiscountAdapter(new ArrayList<Object>(discountList));
        discountsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        discountsRecyclerView.setAdapter(discountAdapter);

        // Load dữ liệu
        loadShopDetails();
        loadShopProducts();
    }

    private void loadShopDetails() {
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getShopDetails(shopSlug, new ApiResponseListener<ShopResponse>() {
            @Override
            public void onSuccess(ShopResponse response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null &&
                        response.getMetadata().getMetadata() != null) {
                    ShopResponse.Shop shop = response.getMetadata().getMetadata().getShop();
                    shopName.setText(shop.getName());
                    shopDescription.setText(shop.getDescription());
                    Glide.with(ShopActivity.this).load(shop.getLogo()).into(shopLogo);
                    discountList.clear();
                    discountList.addAll(response.getMetadata().getMetadata().getDiscounts());
                    discountAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ShopActivity.this, "Dữ liệu shop không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShopActivity.this, "Lỗi tải chi tiết shop: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadShopProducts() {
        progressBar.setVisibility(View.VISIBLE);
        apiManager.getShopProducts(shopSlug, lastSortValues, new ApiResponseListener<ShopProductsResponse>() {
            @Override
            public void onSuccess(ShopProductsResponse response) {
                progressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null &&
                        response.getMetadata().getMetadata() != null) {
                    List<SearchProduct> searchProducts = response.getMetadata().getMetadata().getData();
                    for (SearchProduct searchProduct : searchProducts) {
                        productList.add(searchProduct.toProduct());
                    }
                    productAdapter.notifyDataSetChanged();
                    lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                } else {
                    Toast.makeText(ShopActivity.this, "Dữ liệu sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShopActivity.this, "Lỗi tải sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}