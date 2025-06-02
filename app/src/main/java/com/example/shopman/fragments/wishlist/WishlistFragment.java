package com.example.shopman.fragments.wishlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.models.wishlist.WishlistItem;
import com.example.shopman.models.wishlist.WishlistResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.MyPreferences;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivSearch;
    private TextView itemCount;
    private RecyclerView wishlistRecyclerView;
    private ProgressBar progressBar;
    private ProgressBar loadMoreProgress;
    private TextView emptyView;
    private ProductAdapter wishlistAdapter;
    private List<Product> totalItems;
    private List<Product> wishlistItems;
    private ApiManager apiManager;
    private boolean isLoading;
    private boolean isLastPage;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private static final String TAG = "WishlistFragment";

    public WishlistFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        itemCount = view.findViewById(R.id.itemCount);
        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        loadMoreProgress = view.findViewById(R.id.loadMoreProgress);
        emptyView = view.findViewById(R.id.emptyView);

        if (etSearch == null || ivSearch == null || itemCount == null || wishlistRecyclerView == null ||
                progressBar == null || loadMoreProgress == null || emptyView == null) {
            Log.e(TAG, "One or more views are null, check fragment_wishlist.xml");
            throw new IllegalStateException("Required views not found in fragment_wishlist.xml");
        }

        ivSearch.setOnClickListener(v -> {
            Log.d(TAG, "Search button clicked");
            searchItem();
        });

        totalItems = new ArrayList<>();
        wishlistItems = new ArrayList<>();

        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        wishlistAdapter = new ProductAdapter(requireContext(), new ArrayList<>(), "wishlist");
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        apiManager = new ApiManager(requireContext());

        wishlistRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadMoreItems();
                    }
                }
            }
        });

        loadWishlistData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        wishlistAdapter.setOnProductClickListener(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Toast.makeText(requireContext(), "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            public void onRemoveWishlist(Product product) {
                String accessToken = MyPreferences.getString(requireContext(), "access_token", null);
                if (accessToken == null) {
                    Toast.makeText(requireContext(), "Please log in to remove item", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Gọi API xóa nếu có
                wishlistItems.remove(product);
                totalItems.remove(product);
                wishlistAdapter.notifyDataSetChanged();
                itemCount.setText(wishlistItems.size() + " Items");
                if (wishlistItems.isEmpty()) {
                    showEmpty(true);
                }
            }
        });
    }

    private void loadWishlistData() {
        String accessToken = MyPreferences.getString(requireContext(), "access_token", null);
        if (accessToken == null) {
            Toast.makeText(requireContext(), "Please log in to view your wishlist", Toast.LENGTH_LONG).show();
            showEmpty(true);
            return;
        }

        isLoading = true;
        showLoading(true);
        hideEmpty();

        apiManager.getWishlist(accessToken, currentPage, PAGE_SIZE, new ApiResponseListener<WishlistResponse>() {
            @Override
            public void onSuccess(WishlistResponse response) {
                isLoading = false;
                hideLoading();
                hideLoadMore();

                try {
                    if (response == null || response.getMetadata() == null || response.getMetadata().getMetadata() == null) {
                        showError("Invalid wishlist data");
                        showEmpty(true);
                        return;
                    }

                    List<WishlistItem> wishlistItemsResponse = response.getMetadata().getMetadata().getProducts();
                    int totalItemsCount = response.getMetadata().getMetadata().getTotalItems();
                    int totalPages = response.getMetadata().getMetadata().getTotalPages();

                    if (wishlistItemsResponse == null || wishlistItemsResponse.isEmpty()) {
                        showEmpty(true);
                        isLastPage = true;
                        itemCount.setText("0 Items");
                        return;
                    }

                    List<Product> products = new ArrayList<>();
                    for (WishlistItem item : wishlistItemsResponse) {
                        SearchProduct searchProduct = item.getProduct();
                        if (searchProduct != null) {
                            Product product = searchProduct.toProduct();
                            products.add(product);
                        }
                    }

                    if (currentPage == 1) {
                        totalItems.clear();
                        wishlistItems.clear();
                    }
                    totalItems.addAll(products);
                    wishlistItems.addAll(products);
                    wishlistAdapter.updateProducts(wishlistItems);

                    itemCount.setText(totalItemsCount + " Items");
                    hideEmpty();

                    if (currentPage >= totalPages) {
                        isLastPage = true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing wishlist response: " + e.getMessage(), e);
                    showError("Error processing wishlist data");
                    showEmpty(true);
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                hideLoading();
                hideLoadMore();
                Log.e(TAG, "Wishlist error: " + errorMessage);
                showError(errorMessage);
                showEmpty(true);
                if (errorMessage.contains("Session expired")) {
                    Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadMoreItems() {
        if (isLastPage || isLoading) return;

        currentPage++;
        loadMoreProgress.setVisibility(View.VISIBLE);
        loadWishlistData();
    }

    private void searchItem() {
        String query = etSearch.getText().toString().toLowerCase().trim();
        List<Product> result = new ArrayList<>();

        if (query.isEmpty()) {
            result = totalItems;
            currentPage = 1;
            isLastPage = false;
        } else {
            for (Product product : totalItems) {
                if (product.getName().toLowerCase().contains(query)) {
                    result.add(product);
                }
            }
        }

        wishlistItems.clear();
        wishlistItems.addAll(result);
        wishlistAdapter.updateProducts(wishlistItems);
        itemCount.setText(result.size() + " Items");

        if (result.isEmpty()) {
            showEmpty(true);
        } else {
            hideEmpty();
        }
    }

    private void showLoading(boolean isNewLoad) {
        if (isNewLoad && progressBar != null) {
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

    private void showEmpty(boolean show) {
        if (emptyView != null) {
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (wishlistRecyclerView != null) {
            wishlistRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void hideEmpty() {
        showEmpty(false);
    }
}