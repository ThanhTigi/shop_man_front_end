package com.example.shopman.fragments.search;

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
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        rvProducts = view.findViewById(R.id.rvProducts);
        itemCount = view.findViewById(R.id.itemCount);
        progressBar = view.findViewById(R.id.progressBar);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        emptyView = view.findViewById(R.id.emptyView);

        if (etSearch == null || ivSearch == null || rvProducts == null || itemCount == null ||
                progressBar == null || loadMoreProgress == null || emptyView == null) {
            Log.e(TAG, "One or more views are null, check fragment_search.xml");
            throw new IllegalStateException("Required views not found in fragment_search.xml");
        }

        etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
        etSearch.setThreshold(1);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvProducts.setLayoutManager(layoutManager);
        searchAdapter = new ProductAdapter(requireContext(), productList, "search");
        rvProducts.setAdapter(searchAdapter);

        apiManager = new ApiManager(requireContext());

        ivSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            Log.d(TAG, "Search button clicked with query: " + (query.isEmpty() ? "empty" : query));
            searchProduct(query, true);
        });

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadMoreProducts();
                    }
                }
            }
        });

        // Xử lý click sản phẩm
        searchAdapter.setOnProductClickListener(product -> {
            // Giữ nguyên logic chuyển sang ProductDetailsActivity
            // Bạn có thể dùng product.getSlug() để chuyển
            Log.d(TAG, "Product clicked: " + product.getName());
            // Thêm Intent nếu cần, ví dụ:
            // Intent intent = new Intent(requireContext(), ProductDetailsActivity.class);
            // intent.putExtra("slug", product.getSlug());
            // startActivity(intent);
        });

        Bundle args = getArguments();
        String initialQuery = "";
        if (args != null && args.containsKey("query")) {
            initialQuery = args.getString("query", "");
            etSearch.setText(initialQuery);
        }

        Log.d(TAG, "Initial search with query: " + (initialQuery.isEmpty() ? "empty" : initialQuery));
        searchProduct(initialQuery, true);

        return view;
    }

    public void performSearch(String query) {
        Log.d(TAG, "performSearch called with query: " + (query.isEmpty() ? "empty" : query));
        etSearch.setText(query);
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

            if (isLoading || isLastPage) return;

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
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing search response: " + e.getMessage(), e);
                        showError("Lỗi xử lý kết quả tìm kiếm");
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void hideError() {
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
}