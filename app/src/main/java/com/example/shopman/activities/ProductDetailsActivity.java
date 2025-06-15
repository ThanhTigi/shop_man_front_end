package com.example.shopman.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.adapters.CommentAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Comments.Comment;
import com.example.shopman.models.Comments.CommentResponse;
import com.example.shopman.models.Product;
import com.example.shopman.models.ProductDetails.ProductDetail;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.ProductDetails.Sku;
import com.example.shopman.models.ProductDetails.SpuToSku;
import com.example.shopman.models.Shop.FollowShopResponse;
import com.example.shopman.models.Shop.ShopInfoResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.models.wishlist.Add.WishlistResponse;
import com.example.shopman.models.wishlist.Remove.WishlistRemoveResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.MyPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";
    private static final int COMMENT_PAGE_SIZE = 3;
    private static final int RELATED_PRODUCTS_PAGE_SIZE = 6;

    // Views
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private ImageView ivProduct, ivShopLogo;
    private TextView tvName, tvSubtitle, tvRating, tvPrice, tvOriginalPrice, tvDiscount, tvDescription, tvShopName;
    private RatingBar ratingBar;
    private LinearLayout llAttributesContainer, llRelatedProductsContainer;
    private ChipGroup chipGroupOptions;
    private Button btnFollow, btnViewAllComments;
    private ImageButton btnBack, btnGoToCart, btnBuyNow, btnWishlist;
    private RecyclerView commentsRecyclerView, relatedProductsRecyclerView;
    private ProgressBar relatedProductsProgressBar;

    // Data
    private ApiManager apiManager;
    private CommentAdapter commentAdapter;
    private ProductAdapter relatedProductAdapter;
    private List<Comment> comments = new ArrayList<>();
    private List<Product> relatedProducts = new ArrayList<>();
    private String productId, shopId, productSlug, productThumb, selectedSkuNo;
    private int categoryId;
    private boolean isInWishlist, isShopFollowed;
    private List<Object> lastSortValues;
    private String nextCommentCursor;
    private boolean isLoadingComments, isLoadingRelatedProducts, isLastCommentPage, isLastRelatedPage;
    private ProductDetail productDetail;
    private List<SpuToSku> skus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialize views
        initViews();

        // Get slug from Intent
        productSlug = getIntent().getStringExtra("product_slug");
        Log.d(TAG, "onCreate: productSlug=" + productSlug);
        if (TextUtils.isEmpty(productSlug)) {
            Log.e(TAG, "Product slug is empty or null, finishing activity");
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize managers and adapters
        apiManager = new ApiManager(this);
        initAdapters();
        setupListeners();

        // Load product details
        loadProductDetailsBySlug();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        ivProduct = findViewById(R.id.ivProduct);
        ivShopLogo = findViewById(R.id.ivShopLogo);
        tvName = findViewById(R.id.tvName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        ratingBar = findViewById(R.id.ratingBar);
        tvRating = findViewById(R.id.tvRating);
        tvPrice = findViewById(R.id.tvPrice);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDescription = findViewById(R.id.tvDescription);
        llAttributesContainer = findViewById(R.id.llAttributesContainer);
        chipGroupOptions = findViewById(R.id.chipGroupOptions);
        tvShopName = findViewById(R.id.tvShopName);
        btnFollow = findViewById(R.id.btnFollow);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        btnViewAllComments = findViewById(R.id.btnViewAllComments);
        relatedProductsRecyclerView = findViewById(R.id.relatedProductsRecyclerView);
        relatedProductsProgressBar = findViewById(R.id.relatedProductsProgressBar);
        llRelatedProductsContainer = findViewById(R.id.llRelatedProductsContainer);
        btnBack = findViewById(R.id.btnBack);
        btnGoToCart = findViewById(R.id.btnGoToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnWishlist = findViewById(R.id.btnWishlist);

        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void initAdapters() {
        // Comment Adapter
        commentAdapter = new CommentAdapter(this, comments, null);
        commentAdapter.setMaxComments(COMMENT_PAGE_SIZE);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
        commentsRecyclerView.setNestedScrollingEnabled(false);

        // Related Products Adapter
        relatedProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        relatedProductAdapter = new ProductAdapter(this, relatedProducts, "related");
        relatedProductsRecyclerView.setAdapter(relatedProductAdapter);
        relatedProductAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnViewAllComments.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(productId)) {
                openCommentActivity();
            } else {
                Log.e(TAG, "setupListeners: productId is empty, cannot open CommentActivity");
                Toast.makeText(this, "Không thể mở bình luận, sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });
        btnFollow.setOnClickListener(v -> toggleFollowShop());
        btnGoToCart.setOnClickListener(v -> showSkuSelectionBottomSheet(false));
        btnBuyNow.setOnClickListener(v -> showSkuSelectionBottomSheet(true));
        btnWishlist.setOnClickListener(v -> toggleWishlist());

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    View contentView = nestedScrollView.getChildAt(0);
                    if (contentView != null && nestedScrollView.getHeight() + scrollY >= contentView.getHeight() - 100) {
                        if (!isLoadingRelatedProducts && !isLastRelatedPage) {
                            loadMoreRelatedProducts();
                        }
                    }
                }
            }
        });
    }

    private void loadProductDetailsBySlug() {
        relatedProductsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getProductDetail(productSlug, new ApiResponseListener<ProductDetailResponse>() {
            @Override
            public void onSuccess(ProductDetailResponse response) {
                relatedProductsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    productDetail = response.getMetadata().getMetadata();
                    skus = productDetail.getSpuToSkus();
                    productThumb = productDetail.getThumb();
                    productId = String.valueOf(productDetail.getId());
                    shopId = String.valueOf(productDetail.getShopId());
                    categoryId = productDetail.getCategoryId();
                    isInWishlist = productDetail.isInWishlist();
                    btnWishlist.setImageResource(isInWishlist ? R.drawable.ic_full_heart : R.drawable.ic_heart_1);
                    displayProduct(productDetail);
                    loadShopInfo(shopId);
                    loadComments();
                    loadRelatedProducts();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(String errorMessage) {
                relatedProductsProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Load product detail error: slug=" + productSlug + ", error=" + errorMessage);
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải chi tiết sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadShopInfo(String shopId) {
        apiManager.getShopInfo(shopId, new ApiResponseListener<ShopInfoResponse>() {
            @Override
            public void onSuccess(ShopInfoResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    isShopFollowed = response.getMetadata().getMetadata().isFollowing();
                    tvShopName.setText(response.getMetadata().getMetadata().getShop().getName());
                    Glide.with(ProductDetailsActivity.this)
                            .load(response.getMetadata().getMetadata().getShop().getLogo())
                            .placeholder(R.drawable.ic_placeholder)
                            .into(ivShopLogo);
                    updateFollowButton();
                } else {
                    tvShopName.setText("Shop ID: " + shopId);
                    ivShopLogo.setImageResource(R.drawable.ic_placeholder);
                    btnFollow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Load shop info error: " + errorMessage);
                tvShopName.setText("Shop ID: " + shopId);
                ivShopLogo.setImageResource(R.drawable.ic_placeholder);
                btnFollow.setVisibility(View.GONE);
            }
        });
    }

    private void updateFollowButton() {
        btnFollow.setText(isShopFollowed ? "Unfollow" : "Follow");
        btnFollow.setSelected(isShopFollowed);
        btnFollow.setTextColor(isShopFollowed ? getResources().getColor(android.R.color.black, null) : getResources().getColor(android.R.color.white, null));
    }

    private void toggleFollowShop() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (TextUtils.isEmpty(accessToken)) {
            Toast.makeText(this, "Vui lòng đăng nhập để theo dõi shop", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        ApiResponseListener<FollowShopResponse> listener = new ApiResponseListener<FollowShopResponse>() {
            @Override
            public void onSuccess(FollowShopResponse response) {
                isShopFollowed = !isShopFollowed;
                updateFollowButton();
                Toast.makeText(ProductDetailsActivity.this, isShopFollowed ? "Đã theo dõi shop" : "Đã hủy theo dõi shop", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Toggle follow shop error: " + errorMessage);
                Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        };

        if (isShopFollowed) {
            apiManager.unfollowShop(shopId, listener);
        } else {
            apiManager.followShop(shopId, listener);
        }
    }

    private void toggleWishlist() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (TextUtils.isEmpty(accessToken)) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        int productIdInt;
        try {
            productIdInt = Integer.parseInt(productId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid productId: " + productId, e);
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isInWishlist) {
            apiManager.removeFromWishlist(accessToken, productIdInt, new ApiResponseListener<WishlistRemoveResponse>() {
                @Override
                public void onSuccess(WishlistRemoveResponse response) {
                    isInWishlist = false;
                    btnWishlist.setImageResource(R.drawable.ic_heart_1);
                    Toast.makeText(ProductDetailsActivity.this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Remove from wishlist error: " + errorMessage);
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiManager.addToWishlist(accessToken, productIdInt, new ApiResponseListener<WishlistResponse>() {
                @Override
                public void onSuccess(WishlistResponse response) {
                    isInWishlist = true;
                    btnWishlist.setImageResource(R.drawable.ic_full_heart);
                    Toast.makeText(ProductDetailsActivity.this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, "Add to wishlist error: " + errorMessage);
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showSkuSelectionBottomSheet(boolean isBuyNow) {
        if (skus == null || skus.isEmpty()) {
            Toast.makeText(this, "Không có biến thể nào khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_sku_selection, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageView ivProductThumb = bottomSheetView.findViewById(R.id.ivProductThumb);
        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);
        ChipGroup chipGroupSkus = bottomSheetView.findViewById(R.id.chipGroupSkus);
        TextView tvQuantity = bottomSheetView.findViewById(R.id.tvQuantity);
        Button btnDecrease = bottomSheetView.findViewById(R.id.btnDecrease);
        Button btnIncrease = bottomSheetView.findViewById(R.id.btnIncrease);
        Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);

        Glide.with(this).load(productThumb).placeholder(R.drawable.ic_placeholder).into(ivProductThumb);
        tvTitle.setText(isBuyNow ? "Chọn biến thể để mua ngay" : "Chọn biến thể để thêm vào giỏ");
        btnConfirm.setText(isBuyNow ? "Mua ngay" : "Thêm vào giỏ");

        chipGroupSkus.removeAllViews();
        for (SpuToSku spuToSku : skus) {
            if ("active".equals(spuToSku.getSku().getStatus())) {
                String displayText = formatSkuDisplay(spuToSku.getSku());
                Chip chip = new Chip(this);
                chip.setText(displayText);
                chip.setTextSize(14);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setChipStrokeWidth(2);
                chip.setChipStrokeColorResource(R.color.chip_stroke);
                chip.setTextColor(getResources().getColor(R.color.black, null));

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedSkuNo = spuToSku.getSku().getSku_no();
                        tvQuantity.setText("1");
                    }
                });
                chipGroupSkus.addView(chip);
            }
        }

        if (chipGroupSkus.getChildCount() > 0) {
            Chip firstChip = (Chip) chipGroupSkus.getChildAt(0);
            firstChip.setChecked(true);
            selectedSkuNo = skus.get(0).getSku().getSku_no();
            tvQuantity.setText("1");
        } else {
            bottomSheetDialog.dismiss();
            return;
        }

        final int[] quantity = {1};
        tvQuantity.setText(String.valueOf(quantity[0]));
        btnDecrease.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvQuantity.setText(String.valueOf(quantity[0]));
            }
        });
        btnIncrease.setOnClickListener(v -> {
            int maxStock = getStockForSelectedSku(selectedSkuNo);
            if (quantity[0] < maxStock) {
                quantity[0]++;
                tvQuantity.setText(String.valueOf(quantity[0]));
            } else {
                Toast.makeText(this, "Số lượng tối đa: " + maxStock, Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirm.setOnClickListener(v -> {
            if (selectedSkuNo == null) {
                Toast.makeText(this, "Vui lòng chọn một biến thể", Toast.LENGTH_SHORT).show();
                return;
            }

            String accessToken = MyPreferences.getString(this, "access_token", null);
            if (TextUtils.isEmpty(accessToken)) {
                Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                bottomSheetDialog.dismiss();
                return;
            }

            if (isBuyNow) {
                Toast.makeText(this, "Mua ngay với SKU: " + selectedSkuNo + ", Số lượng: " + quantity[0], Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                apiManager.addToCart(accessToken, productId, selectedSkuNo, quantity[0], new ApiResponseListener<com.example.shopman.models.cart.CartAddResponse>() {
                    @Override
                    public void onSuccess(com.example.shopman.models.cart.CartAddResponse response) {
                        Toast.makeText(ProductDetailsActivity.this, response.getMetadata().getMessage(), Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Add to cart error: " + errorMessage);
                        Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bottomSheetDialog.show();
    }

    private String formatSkuDisplay(Sku sku) {
        StringBuilder display = new StringBuilder();
        if (sku.getSkuAttr() != null && sku.getSkuAttr().getSku_attrs() != null) {
            Map<String, String> attrs = sku.getSkuAttr().getSku_attrs();
            if (attrs.containsKey("size")) display.append("Size: ").append(attrs.get("size")).append(", ");
            if (attrs.containsKey("color")) display.append("Color: ").append(attrs.get("color")).append(", ");
        }
        display.append("Kho: ").append(sku.getSku_stock());
        return display.toString().trim().replaceAll(",$", "");
    }

    private int getStockForSelectedSku(String skuNo) {
        if (skuNo == null) return 1;
        for (SpuToSku spuToSku : skus) {
            if (spuToSku.getSku().getSku_no().equals(skuNo)) {
                return spuToSku.getSku().getSku_stock();
            }
        }
        return 1;
    }

    private void loadComments() {
        if (isLoadingComments) return;
        if (TextUtils.isEmpty(productId)) {
            Log.e(TAG, "loadComments: productId is null or empty, skipping comment loading");
            return;
        }

        isLoadingComments = true;
        try {
            int productIdInt = Integer.parseInt(productId);
            apiManager.getProductComments(productIdInt, COMMENT_PAGE_SIZE, null, new ApiResponseListener<CommentResponse>() {
                @Override
                public void onSuccess(CommentResponse response) {
                    isLoadingComments = false;
                    if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                        List<Comment> newComments = response.getMetadata().getMetadata().getComments();
                        nextCommentCursor = response.getMetadata().getMetadata().getNextCursor();
                        if (newComments != null) {
                            comments.clear();
                            comments.addAll(newComments);
                            commentAdapter.updateComments(comments);
                        }
                    } else {
                        Log.e(TAG, "Invalid comment response: response=" + response);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    isLoadingComments = false;
                    Log.e(TAG, "Load comments error: productId=" + productId + ", error=" + errorMessage);
                }
            });
        } catch (NumberFormatException e) {
            isLoadingComments = false;
            Log.e(TAG, "loadComments: Invalid productId format: " + productId, e);
        }
    }

    private void openCommentActivity() {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("productId", productId);
        startActivity(intent);
    }

    private void loadRelatedProducts() {
        if (isLoadingRelatedProducts) return;
        isLoadingRelatedProducts = true;
        relatedProductsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getRelatedProducts(categoryId, lastSortValues, RELATED_PRODUCTS_PAGE_SIZE, new ApiResponseListener<SearchProductsResponse>() {
            @Override
            public void onSuccess(SearchProductsResponse response) {
                isLoadingRelatedProducts = false;
                relatedProductsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<SearchProduct> products = response.getMetadata().getMetadata().getData();
                    lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                    int total = response.getMetadata().getMetadata().getTotal();
                    if (products != null && !products.isEmpty()) {
                        relatedProducts.addAll(convertToProducts(products));
                        relatedProductAdapter.notifyDataSetChanged();
                        llRelatedProductsContainer.setVisibility(View.VISIBLE);
                        isLastRelatedPage = products.size() < RELATED_PRODUCTS_PAGE_SIZE || relatedProducts.size() >= total;
                    } else {
                        llRelatedProductsContainer.setVisibility(View.GONE);
                        isLastRelatedPage = true;
                    }
                } else {
                    llRelatedProductsContainer.setVisibility(View.GONE);
                    isLastRelatedPage = true;
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoadingRelatedProducts = false;
                relatedProductsProgressBar.setVisibility(View.GONE);
                llRelatedProductsContainer.setVisibility(View.GONE);
                Log.e(TAG, "Load related products error: " + errorMessage);
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải sản phẩm liên quan: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreRelatedProducts() {
        if (!isLoadingRelatedProducts && !isLastRelatedPage && categoryId != 0) {
            loadRelatedProducts();
        }
    }

    private void displayProduct(ProductDetail productDetail) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        long price = Long.parseLong(productDetail.getPrice());

        tvName.setText(productDetail.getName());
        tvSubtitle.setText(productDetail.getDesc_plain());
        tvRating.setText(String.format("%d đánh giá", productDetail.getSale_count()));
        tvPrice.setText(String.format("đ%s", formatter.format(price)));
        tvOriginalPrice.setText(String.format("đ%s", formatter.format(price * 2))); // Placeholder for original price
        tvDiscount.setText(String.format("%d%% Giảm", productDetail.getDiscount_percentage()));
        ratingBar.setRating(productDetail.getRating());
        tvDescription.setText(productDetail.getDesc());

        Glide.with(this).load(productThumb).placeholder(R.drawable.ic_placeholder).into(ivProduct);

        // Attributes
        llAttributesContainer.removeAllViews();
        Map<String, Object> attrs = productDetail.getAttrs();
        if (attrs != null && !attrs.isEmpty()) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                TextView title = new TextView(this);
                title.setText(capitalize(entry.getKey()));
                title.setTextSize(16);
                llAttributesContainer.addView(title);

                ChipGroup chipGroup = new ChipGroup(this);
                chipGroup.setSingleSelection(true);
                if (entry.getValue() instanceof List) {
                    for (Object item : (List<?>) entry.getValue()) {
                        Chip chip = new Chip(this);
                        chip.setText(item.toString());
                        chip.setCheckable(true);
                        chipGroup.addView(chip);
                    }
                }
                llAttributesContainer.addView(chipGroup);
            }
        }
    }

    private String capitalize(String str) {
        if (TextUtils.isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private List<Product> convertToProducts(List<SearchProduct> searchProducts) {
        List<Product> products = new ArrayList<>();
        for (SearchProduct sp : searchProducts) {
            Product product = new Product(
                    String.valueOf(sp.getId()),
                    sp.getName(),
                    Long.parseLong(sp.getPrice().replaceAll("[^0-9]", "")),
                    sp.getThumb(),
                    sp.getRating(),
                    sp.getDiscountPercentage(),
                    sp.getSlug(),
                    sp.getSaleCount(),
                    sp.getDesc()
            );
            products.add(product);
        }
        return products;
    }
}