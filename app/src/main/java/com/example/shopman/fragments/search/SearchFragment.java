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

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;
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
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Khởi tạo views
        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        rvProducts = view.findViewById(R.id.rvProducts);
        itemCount = view.findViewById(R.id.itemCount);
        progressBar = view.findViewById(R.id.progressBar);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        emptyView = view.findViewById(R.id.emptyView);

        // Kiểm tra null cho các view
        if (etSearch == null || ivSearch == null || rvProducts == null || itemCount == null ||
                progressBar == null || loadMoreProgress == null || emptyView == null) {
            Log.e(TAG, "One or more views are null, check fragment_search.xml");
            throw new IllegalStateException("Required views not found in fragment_search.xml");
        }

        // Khởi tạo adapter cho suggestions
        etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
        etSearch.setThreshold(1);

        // Khởi tạo RecyclerView với GridLayoutManager (2 cột)
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        rvProducts.setLayoutManager(layoutManager);
        searchAdapter = new ProductAdapter(productList);
        rvProducts.setAdapter(searchAdapter);

        apiManager = new ApiManager(requireContext());

        // Xử lý sự kiện nhấn nút tìm kiếm
        ivSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            searchProduct(query, true); // Gọi tìm kiếm ngay cả khi query rỗng
        });

        // Xử lý phân trang
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Chỉ load khi cuộn xuống
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadMoreProducts();
                    }
                }
            }
        });

        // Nhận từ khóa từ HomeFragment
        Bundle args = getArguments();
        if (args != null && args.containsKey("query")) {
            String query = args.getString("query");
            if (query != null && !query.isEmpty()) {
                etSearch.setText(query);
                searchProduct(query, true);
            }
        }

        return view;
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
                            showError("Invalid response data");
                            showEmpty(true);
                            return;
                        }

                        List<SearchProduct> searchProducts = response.getMetadata().getMetadata().getData();
                        lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                        int total = response.getMetadata().getMetadata().getTotal();
                        List<String> newSuggestions = response.getMetadata().getMetadata().getSuggest();

                        if (searchProducts == null || searchProducts.isEmpty()) {
                            showError("No products found");
                            showEmpty(true);
                            isLastPage = true;
                            return;
                        }

                        if (newSuggestions != null && !newSuggestions.isEmpty() && isNewSearch) {
                            suggestions.clear();
                            suggestions.addAll(newSuggestions);
                            etSearch.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions));
                        }

                        List<Product> products = mapSearchProductsToProducts(searchProducts);
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
                        showError("Error processing search results");
                        showEmpty(true);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    hideLoading();
                    hideLoadMore();
                    Log.e(TAG, "Search error: " + errorMessage);
                    showError(errorMessage);
                    showEmpty(true);
                    if (errorMessage.contains("Session expired")) {
                        Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            isLoading = false;
            Log.e(TAG, "Error in searchProduct: " + e.getMessage(), e);
            hideLoading();
            hideLoadMore();
            showError("Search failed: " + e.getMessage());
            showEmpty(true);
        }
    }

    private void loadMoreProducts() {
        if (currentQuery != null) {
            loadMoreProgress.setVisibility(View.VISIBLE);
            searchProduct(currentQuery, false);
        }
    }

    private List<Product> mapSearchProductsToProducts(List<SearchProduct> searchProducts) {
        List<Product> products = new ArrayList<>();
        for (SearchProduct sp : searchProducts) {
            Product product = new Product();
            product.setId(sp.getId());
            product.setName(sp.getName());
            product.setDesc(sp.getDesc());
            product.setDesc_plain(sp.getDesc_plain());
            product.setPrice(sp.getPrice());
            product.setThumb(sp.getThumb());
            product.setRating(sp.getRating());
            product.setDiscount_percentage(sp.getDiscount_percentage());
            product.setSlug(sp.getSlug());
            product.setCategoryId(sp.getCategoryId());
            product.setShopId(sp.getShopId());
            product.setSale_count(sp.getSale_count());
            product.setCategoryPath(null);
            product.setAttrs(null);
            product.setSpuToSkus(null);
            product.setHas_variations(false);
            products.add(product);
        }
        return products;
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
        // Không cần ẩn Toast
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