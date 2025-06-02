package com.example.shopman.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Product;
import com.example.shopman.models.ProductDetails.ProductDetail;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.ProductDetails.Sku;
import com.example.shopman.models.ProductDetails.SpuToSku;
import com.example.shopman.models.category.CategoryProductResponse;
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
    private static final int PAGE_SIZE = 10;

    private ImageView ivProduct, ivShopLogo;
    private TextView tvName, tvSubtitle, tvRating, tvPrice, tvOriginalPrice, tvDiscount, tvDescription, tvShopName;
    private RatingBar ratingBar;
    private LinearLayout llAttributesContainer;
    private ImageButton btnGoToCart, btnBuyNow, btnWishlist;
    private Button btnFollow;
    private RecyclerView rvRelatedProducts;
    private ApiManager apiManager;
    private ProductAdapter relatedProductAdapter;
    private Map<String, String> selectedAttributes = new HashMap<>();
    private String selectedSkuNo;
    private List<Product> relatedProducts = new ArrayList<>();
    private List<SpuToSku> skus;
    private boolean isLoadingMore = false;
    private boolean isInWishlist = false;
    private int currentPage = 1;
    private int totalPages = 1;
    private String productThumb;
    private String productId;
    private String categoryId;
    private ProductDetail productDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Khởi tạo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }

        // Khởi tạo views
        ivProduct = findViewById(R.id.ivProduct);
        ivShopLogo = findViewById(R.id.ivShopLogo);
        tvName = findViewById(R.id.tvName);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvRating = findViewById(R.id.tvRating);
        tvPrice = findViewById(R.id.tvPrice);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDescription = findViewById(R.id.tvDescription);
        tvShopName = findViewById(R.id.tvShopName);
        ratingBar = findViewById(R.id.ratingBar);
        llAttributesContainer = findViewById(R.id.llAttributesContainer);
        btnGoToCart = findViewById(R.id.btnGoToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnFollow = findViewById(R.id.btnFollow);
        btnWishlist = findViewById(R.id.btnWishlist);
        rvRelatedProducts = findViewById(R.id.rvRelatedProducts);

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Cài đặt RecyclerView cho sản phẩm liên quan
        rvRelatedProducts.setLayoutManager(new GridLayoutManager(this, 2));
        relatedProductAdapter = new ProductAdapter(this, relatedProducts, "related");
        rvRelatedProducts.setAdapter(relatedProductAdapter);
        rvRelatedProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoadingMore && currentPage <= totalPages) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastVisibleItemPosition() >= relatedProducts.size() - 2) {
//                        loadMoreRelatedProducts();
                    }
                }
            }
        });

        // Sự kiện click
        btnGoToCart.setOnClickListener(v -> showSkuSelectionBottomSheet(false));
        btnBuyNow.setOnClickListener(v -> showSkuSelectionBottomSheet(true));
        btnFollow.setOnClickListener(v -> {
            btnFollow.setText("Đang theo dõi");
            btnFollow.setEnabled(false);
            Toast.makeText(this, "Đã theo dõi cửa hàng", Toast.LENGTH_SHORT).show();
        });
        btnWishlist.setOnClickListener(v -> toggleWishlist());

        // Lấy slug từ Intent
        String slug = getIntent().getStringExtra("product_slug");
        if (slug != null && !slug.isEmpty()) {
            loadProductDetail(slug);
        } else {
            Toast.makeText(this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void toggleWishlist() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (accessToken == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (productId == null) {
            Toast.makeText(this, "Không thể lấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        int productIdInt;
        try {
            productIdInt = Integer.parseInt(productId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        apiManager.addToWishlist(accessToken, productIdInt, new ApiResponseListener<com.example.shopman.models.wishlist.WishlistResponse>() {
            @Override
            public void onSuccess(com.example.shopman.models.wishlist.WishlistResponse response) {
                isInWishlist = !isInWishlist;
                btnWishlist.setImageResource(isInWishlist ? R.drawable.ic_full_heart : R.drawable.ic_heart_1);
                Toast.makeText(ProductDetailsActivity.this, isInWishlist ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Wishlist error: " + errorMessage);
            }
        });
    }

    private void showSkuSelectionBottomSheet(boolean isBuyNow) {
        if (skus == null || skus.isEmpty()) {
            Toast.makeText(this, "Không có biến thể nào khả dụng", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No SKUs available");
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

        // Hiển thị hình ảnh sản phẩm
        Glide.with(this)
                .load(productThumb)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivProductThumb);

        tvTitle.setText(isBuyNow ? "Chọn biến thể để mua ngay" : "Chọn biến thể để thêm vào giỏ");
        btnConfirm.setText(isBuyNow ? "Mua ngay" : "Thêm vào giỏ");

        // Tạo các chip cho biến thể
        chipGroupSkus.removeAllViews();
        for (SpuToSku spuToSku : skus) {
            Sku sku = spuToSku.getSku();
            if (sku.getStatus().equals("active")) {
                String displayText = formatSkuDisplay(sku);
                Chip chip = new Chip(this);
                chip.setText(displayText);
                chip.setTextSize(14);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setChipStrokeWidth(2);
                chip.setChipStrokeColor(new ColorStateList(
                        new int[][]{{android.R.attr.state_checked}, {}},
                        new int[]{getResources().getColor(R.color.pink), getResources().getColor(R.color.chip_stroke)}
                ));
                chip.setTextColor(getResources().getColor(R.color.black));

                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedSkuNo = sku.getSku_no();
                        tvQuantity.setText("1");
                    }
                });
                chipGroupSkus.addView(chip);
            }
        }

        // Chọn chip đầu tiên mặc định
        if (chipGroupSkus.getChildCount() > 0) {
            Chip firstChip = (Chip) chipGroupSkus.getChildAt(0);
            firstChip.setChecked(true);
            selectedSkuNo = skus.get(0).getSku().getSku_no();
            tvQuantity.setText("1");
        } else {
            Toast.makeText(this, "Không có biến thể nào khả dụng", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
            return;
        }

        // Xử lý số lượng
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

        // Xử lý nút xác nhận
        btnConfirm.setOnClickListener(v -> {
            if (selectedSkuNo == null) {
                Toast.makeText(this, "Vui lòng chọn một biến thể", Toast.LENGTH_SHORT).show();
                return;
            }

            String accessToken = MyPreferences.getString(this, "access_token", null);
            if (accessToken == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                bottomSheetDialog.dismiss();
                return;
            }

            if (isBuyNow) {
//                // Chuyển đến CheckoutActivity
//                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
//                checkoutIntent.putExtra("product_id", productId);
//                checkoutIntent.putExtra("sku_no", selectedSkuNo);
//                checkoutIntent.putExtra("quantity", quantity[0]);
//                startActivity(checkoutIntent);
//                bottomSheetDialog.dismiss();
            } else {
                apiManager.addToCart(accessToken, productId, selectedSkuNo, quantity[0], new ApiResponseListener<com.example.shopman.models.cart.CartAddResponse>() {
                    @Override
                    public void onSuccess(com.example.shopman.models.cart.CartAddResponse response) {
                        Toast.makeText(ProductDetailsActivity.this, response.getMetadata().getMessage(), Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Add to cart error: " + errorMessage);
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

    private void loadProductDetail(String slug) {
        apiManager.getProductDetail(slug, new ApiResponseListener<ProductDetailResponse>() {
            @Override
            public void onSuccess(ProductDetailResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    productDetail = response.getMetadata().getMetadata();
                    skus = productDetail.getSpuToSkus();
                    productThumb = productDetail.getThumb();
                    productId = String.valueOf(productDetail.getId());
                    categoryId = String.valueOf(productDetail.getCategoryId());
                    Log.d(TAG, "SKUs loaded: " + (skus != null ? skus.size() : 0));
                    displayProduct(productDetail);
                    if (categoryId != null) {
//                        loadRelatedProducts(categoryId);
                    } else {
                        Log.e(TAG, "Category ID is null");
                        Toast.makeText(ProductDetailsActivity.this, "Không thể tải sản phẩm liên quan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invalid response");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải chi tiết sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error: " + errorMessage);
            }
        });
    }

    private void displayProduct(ProductDetail productDetail) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        long price;
        try {
            price = Long.parseLong(productDetail.getPrice());
        } catch (NumberFormatException e) {
            price = 0L;
            Log.e(TAG, "Invalid price format: " + productDetail.getPrice());
        }

        tvName.setText(productDetail.getName() != null ? productDetail.getName() : "Không xác định");
        tvSubtitle.setText(productDetail.getDesc_plain() != null ? productDetail.getDesc_plain() : "");
        tvRating.setText(String.format("%d đánh giá", productDetail.getSale_count()));
        tvPrice.setText(String.format("đ%s", formatter.format(price)));
        tvOriginalPrice.setText(String.format("đ%s", formatter.format(price * 2)));
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        tvDiscount.setText(String.format("%d%% Giảm", productDetail.getDiscount_percentage()));
        ratingBar.setRating(productDetail.getRating());
        tvDescription.setText(productDetail.getDesc() != null ? productDetail.getDesc() : "Không có mô tả");
        tvShopName.setText("Tên cửa hàng (ShopId: " + productDetail.getShopId() + ")");

        // Tải hình ảnh sản phẩm
        Glide.with(this)
                .load(productThumb)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivProduct);

        // Tải logo cửa hàng (giả sử placeholder)
        ivShopLogo.setImageResource(R.drawable.ic_placeholder);

        // Hiển thị thuộc tính sản phẩm
        llAttributesContainer.removeAllViews();
        Map<String, Object> attrs = productDetail.getAttrs();
        if (attrs != null && !attrs.isEmpty()) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Tiêu đề thuộc tính
                TextView title = new TextView(this);
                title.setText(capitalize(key));
                title.setTextSize(16);
                title.setTextColor(getResources().getColor(R.color.black));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                title.setPadding(0, 16, 0, 8);
                llAttributesContainer.addView(title);

                // Nhóm chip cho các giá trị
                ChipGroup chipGroup = new ChipGroup(this);
                chipGroup.setSingleSelection(true);
                chipGroup.setChipSpacingHorizontal(8);
                chipGroup.setChipSpacingVertical(8);

                if (value instanceof List) {
                    List<?> listValues = (List<?>) value;
                    for (Object item : listValues) {
                        if (item instanceof String) {
                            Chip chip = new Chip(this);
                            chip.setText((String) item);
                            chip.setTextSize(14);
                            chip.setCheckable(true);
                            chip.setChipBackgroundColorResource(R.color.chip_background);
                            chip.setChipStrokeColorResource(R.color.chip_stroke);
                            chip.setChipStrokeWidth(1);
                            chip.setCheckedIcon(null);
                            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if (isChecked) {
                                    selectedAttributes.put(key, (String) item);
                                } else {
                                    selectedAttributes.remove(key);
                                }
                            });
                            chipGroup.addView(chip);
                        }
                    }
                } else if (value instanceof String) {
                    Chip chip = new Chip(this);
                    chip.setText((String) value);
                    chip.setTextSize(14);
                    chip.setCheckable(false);
                    chip.setChipBackgroundColorResource(R.color.chip_background);
                    chip.setChipStrokeColorResource(R.color.chip_stroke);
                    chip.setChipStrokeWidth(1);
                    chip.setCheckedIcon(null);
                    chipGroup.addView(chip);
                }
                llAttributesContainer.addView(chipGroup);
            }
        } else {
            TextView noAttrs = new TextView(this);
            noAttrs.setText("Không có thuộc tính nào");
            noAttrs.setTextSize(14);
            noAttrs.setPadding(0, 8, 0, 8);
            llAttributesContainer.addView(noAttrs);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

//    private void loadRelatedProducts(String categoryId) {
//        isLoadingMore = true;
//        findViewById(R.id.loadMoreProgress).setVisibility(View.VISIBLE);
//        apiManager.getCategoryProducts(categoryId, currentPage, PAGE_SIZE, new ApiResponseListener<CategoryProductResponse>() {
//            @Override
//            public void onSuccess(CategoryProductResponse response) {
//                isLoadingMore = false;
//                findViewById(R.id.loadMoreProgress).setVisibility(View.GONE);
//                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
//                    List<Product> newProducts = response.getMetadata().getMetadata().getProducts();
//                    totalPages = response.getMetadata().getMetadata().getTotalPages();
//                    if (newProducts != null && !newProducts.isEmpty()) {
//                        relatedProducts.addAll(newProducts);
//                        relatedProductAdapter.notifyItemRangeInserted(relatedProducts.size() - newProducts.size(), newProducts.size());
//                        currentPage++;
//                    } else {
//                        Toast.makeText(ProductDetailsActivity.this, "Không còn sản phẩm liên quan", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(ProductDetailsActivity.this, "Không thể tải sản phẩm liên quan", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                isLoadingMore = false;
//                findViewById(R.id.loadMoreProgress).setVisibility(View.GONE);
//                Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Load related products error: " + errorMessage);
//            }
//        });
//    }

//    private void loadMoreRelatedProducts() {
//        if (!isLoadingMore && currentPage <= totalPages && categoryId != null) {
//            loadRelatedProducts(categoryId);
//        }
//    }
}