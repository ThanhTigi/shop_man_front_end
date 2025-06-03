package com.example.shopman.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.activities.ProductDetailsActivity;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductsFragment extends Fragment {
    private static final String TAG = "CategoryProductsFragment";
    private static final String ARG_SLUG = "slug";
    private static final String ARG_CATEGORY_NAME = "category_name";
    private static final String ARG_CATEGORY_IMAGE = "category_image";
    private static final int PAGE_SIZE = 10;

    private String slug;
    private String categoryName;
    private String categoryImageUrl;
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private ProgressBar loadMoreProgress;
    private TextView emptyView;
    private TextView itemCount;
    private ImageView categoryImage;
    private TextView categoryNameView;
    private ApiManager apiManager;
    private String lastSortValues; // Đổi sang String
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int totalProducts = 0;

    public static CategoryProductsFragment newInstance(String slug, String categoryName, String categoryImageUrl) {
        CategoryProductsFragment fragment = new CategoryProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SLUG, slug);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        args.putString(ARG_CATEGORY_IMAGE, categoryImageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            slug = getArguments().getString(ARG_SLUG);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME, "Category");
            categoryImageUrl = getArguments().getString(ARG_CATEGORY_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called for slug: " + slug);
        return inflater.inflate(R.layout.fragment_category_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        emptyView = view.findViewById(R.id.emptyView);
        itemCount = view.findViewById(R.id.itemCount);
        categoryImage = view.findViewById(R.id.categoryImage);
        categoryNameView = view.findViewById(R.id.categoryName);

        if (getContext() == null) {
            Log.e(TAG, "Context is null, fragment not attached to activity");
            return;
        }

        categoryNameView.setText(categoryName);
        if (categoryImageUrl != null && !categoryImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(categoryImageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(categoryImage);
        } else {
            categoryImage.setImageResource(R.drawable.ic_placeholder);
        }

        apiManager = new ApiManager(requireContext());
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productList, "");
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productsRecyclerView.setAdapter(productAdapter);

        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                int totalHeight = v.getChildAt(0).getHeight();
                int scrollViewHeight = v.getHeight();
                if (!isLoading && !isLastPage && scrollY >= (totalHeight - scrollViewHeight - 200)
                        && productList.size() < totalProducts) {
                    loadMoreProducts();
                }
            });
        }

        productAdapter.setOnProductClickListener(product -> {
            Log.d(TAG, "Product clicked: " + product.getName());
            Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        loadProducts(true);
    }

    private void loadProducts(boolean isNewSearch) {
        if (isLoading || isLastPage) return;
        isLoading = true;

        if (isNewSearch) {
            productList.clear();
            productAdapter.notifyDataSetChanged();
            lastSortValues = null;
            isLastPage = false;
            itemCount.setText("0 Items");
            showEmpty(true);
            showLoading(true);
        } else {
            loadMoreProgress.setVisibility(View.VISIBLE);
        }

        apiManager.getCategoryProducts(slug, lastSortValues, PAGE_SIZE, new ApiResponseListener<SearchProductsResponse>() {
            @Override
            public void onSuccess(SearchProductsResponse response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                loadMoreProgress.setVisibility(View.GONE);
                Log.d(TAG, "Products loaded successfully: " + new Gson().toJson(response));

                if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null || response.getMetadata().getMetadata().getData() == null) {
                    showError("Dữ liệu không hợp lệ");
                    showEmpty(true);
                    return;
                }

                List<SearchProduct> categoryProducts = response.getMetadata().getMetadata().getData();
                totalProducts = response.getMetadata().getMetadata().getTotal();
                lastSortValues = new Gson().toJson(response.getMetadata().getMetadata().getLastSortValues());

                if (categoryProducts.isEmpty()) {
                    showError("Không tìm thấy sản phẩm");
                    showEmpty(true);
                    isLastPage = true;
                    return;
                }

                List<Product> products = new ArrayList<>();
                for (SearchProduct sp : categoryProducts) {
                    products.add(sp.toProduct());
                }

                productList.addAll(products);
                productAdapter.notifyItemRangeInserted(productList.size() - products.size(), products.size());
                itemCount.setText(totalProducts + " Items");

                if (products.size() < PAGE_SIZE) {
                    isLastPage = true;
                }

                showEmpty(false);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                loadMoreProgress.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load products: " + errorMessage);
                showError(errorMessage);
                showEmpty(true);
            }
        });
    }

    private void loadMoreProducts() {
        Log.d(TAG, "Loading more products with lastSortValues: " + lastSortValues);
        loadProducts(false);
    }

    private void showLoading(boolean isNewSearch) {
        if (isNewSearch) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void showEmpty(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        productsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}