package com.example.shopman.fragments.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.activities.ProductDetailsActivity;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.AppConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private AutoCompleteTextView etSearch;
    private ImageView ivSearch;
    private RecyclerView rvProducts;
    private List<String> suggestions = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter searchAdapter;
    private TextView itemCount;
    private ProgressBar progressBar;
    private ProgressBar loadMoreProgress;
    private TextView emptyView;
    private String currentQuery;
    private List<Object> lastSortValues;
    private boolean isLastPage;
    private boolean isLoading;
    private static final int PAGE_SIZE = 20;
    private static final String TAG = "SearchFragment";
    private ApiManager apiManager;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Khởi tạo views
        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        rvProducts = view.findViewById(R.id.rvProducts);
        itemCount = view.findViewById(R.id.itemCount);
        progressBar = view.findViewById(R.id.progressBar);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        emptyView = view.findViewById(R.id.emptyView);

        // Kiểm tra null views
        if (etSearch == null || ivSearch == null || rvProducts == null || itemCount == null ||
                progressBar == null || loadMoreProgress == null || emptyView == null) {
            Log.e(TAG, "One or more views are null, check fragment_search.xml");
            Toast.makeText(requireContext(), "Lỗi giao diện: Thiếu view trong fragment_search.xml", Toast.LENGTH_LONG).show();
            return view;
        }

        // Khởi tạo ApiManager
        apiManager = new ApiManager(requireContext());

        // Khởi tạo adapter trước
        searchAdapter = new ProductAdapter(requireContext(), productList, "search");
        rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvProducts.setAdapter(searchAdapter);

        // Gán listener sau khi khởi tạo adapter
        searchAdapter.setOnProductClickListener(product -> {
            Log.d(TAG, "Product clicked: " + product.getName());
            Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });

        // Cấu hình AutoCompleteTextView
        etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
        etSearch.setThreshold(1);

        // Search listener
        ivSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            Log.d(TAG, "Search button clicked with query: " + (query.isEmpty() ? "empty" : query));
            searchProduct(query, true);
        });

        // Load more listener
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                            loadMoreProducts();
                        }
                    }
                }
            }
        });

        // Kiểm tra AppConfig và Bundle
        String initialQuery = "";
        if (AppConfig.isSearch) {
            initialQuery = AppConfig.keywordSearch != null ? AppConfig.keywordSearch : "";
            AppConfig.isSearch = false; // Reset flag
            Log.d(TAG, "Using query from AppConfig: " + initialQuery);
        } else {
            Bundle args = getArguments();
            if (args != null && args.containsKey("query")) {
                initialQuery = args.getString("query", "");
                Log.d(TAG, "Using query from Bundle: " + initialQuery);
            }
        }

        etSearch.setText(initialQuery);
        Log.d(TAG, "Initial search with query: " + (initialQuery.isEmpty() ? "empty" : initialQuery));
        searchProduct(initialQuery, true);

        return view;
    }

    public void performSearch(String query) {
        Log.d(TAG, "performSearch called with query: " + (query.isEmpty() ? "empty" : query));
        if (etSearch != null) {
            etSearch.setText(query);
        }
        searchProduct(query, true);
    }

    private void searchProduct(String query, boolean isNewSearch) {
        try {
            if (isNewSearch) {
                currentQuery = query;
                productList.clear();
                searchAdapter.notifyDataSetChanged();
                lastSortValues = null;
                isLastPage = false;
                itemCount.setText("0 Items");
                showEmpty(true);
                hideError();
                hideLoading();
            }

            if (isLoading || isLastPage) {
                Log.d(TAG, "Skipping search: isLoading=" + isLoading + ", isLastPage=" + isLastPage);
                return;
            }

            isLoading = true;
            itemCount.setText(productList.size() + " Items");
            showLoading(isNewSearch);
            hideError();
            hideEmpty();

            String lastSortValuesJson = lastSortValues != null ? new Gson().toJson(lastSortValues) : null;
            apiManager.searchProducts(query, lastSortValuesJson, PAGE_SIZE, new ApiResponseListener<SearchProductsResponse>() {
                @Override
                public void onSuccess(SearchProductsResponse response) {
                    isLoading = false;
                    hideLoading();
                    hideLoadMore();
                    Log.d(TAG, "Search response: " + new Gson().toJson(response));
                    try {
                        if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                            Log.w(TAG, "Invalid response data");
                            showError(query.isEmpty() ? "Không thể tải sản phẩm" : "Dữ liệu không hợp lệ");
                            showEmpty(true);
                            return;
                        }

                        List<Product> products = new ArrayList<>();
                        for (SearchProduct searchProduct : response.getMetadata().getMetadata().getData()) {
                            products.add(searchProduct.toProduct());
                        }

                        lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                        int total = response.getMetadata().getMetadata().getTotal();
                        List<String> newSuggestions = response.getMetadata().getMetadata().getSuggest();

                        if (products.isEmpty()) {
                            Log.w(TAG, "No products found for query: " + query);
                            showError(query.isEmpty() ? "Không có sản phẩm nào để hiển thị" : "Không tìm thấy sản phẩm");
                            showEmpty(true);
                            isLastPage = true;
                            return;
                        }

                        if (newSuggestions != null && !newSuggestions.isEmpty() && isNewSearch) {
                            suggestions.clear();
                            suggestions.addAll(newSuggestions);
                            etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
                        }

                        productList.addAll(products);
                        searchAdapter.notifyItemRangeInserted(productList.size() - products.size(), products.size());
                        itemCount.setText(total + " Items");

                        if (products.isEmpty()) {
                            showEmpty(true);
                            isLastPage = true;
                        } else {
                            hideEmpty();
                        }

                        if (products.size() < PAGE_SIZE) {
                            isLastPage = true;
                            Log.d(TAG, "Reached last page");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing search response: " + e.getMessage(), e);
                        showError("Lỗi xử lý kết quả tìm kiếm: " + e.getMessage());
                        showEmpty(true);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    hideLoading();
                    hideLoadMore();
                    Log.e(TAG, "Search error: " + errorMessage);
                    showError(query.isEmpty() ? "Không thể tải sản phẩm, vui lòng thử lại" : errorMessage);
                    showEmpty(true);
                    if (errorMessage.contains("Session expired")) {
                        Log.d(TAG, "Session expired, waiting for logout broadcast");
                        Toast.makeText(requireContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            isLoading = false;
            Log.e(TAG, "Error in searchProduct: " + e.getMessage(), e);
            hideLoading();
            hideLoadMore();
            showError("Tìm kiếm thất bại: " + e.getMessage());
            showEmpty(true);
        }
    }

    private void loadMoreProducts() {
        if (currentQuery != null) {
            Log.d(TAG, "Loading more products for query: " + currentQuery);
            loadMoreProgress.setVisibility(View.VISIBLE);
            searchProduct(currentQuery, false);
        }
    }

    private void showLoading(boolean isNewSearch) {
        if (isNewSearch && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideLoadMore() {
        if (loadMoreProgress != null) {
            loadMoreProgress.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideError() {
        // Không cần triển khai nếu không có view lỗi riêng
    }

    private void showEmpty(boolean show) {
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (rvProducts != null) {
            rvProducts.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void hideEmpty() {
        showEmpty(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView called");
    }
}