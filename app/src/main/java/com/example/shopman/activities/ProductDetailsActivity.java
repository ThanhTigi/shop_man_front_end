package com.example.shopman.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.shopman.models.Comments.RepliesResponse;
import com.example.shopman.models.Product;
import com.example.shopman.models.ProductDetails.ProductDetail;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.ProductDetails.Sku;
import com.example.shopman.models.ProductDetails.SpuToSku;
import com.example.shopman.models.Shop.FollowShopResponse;
import com.example.shopman.models.Shop.ShopInfoResponse;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
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
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailsActivity";
    private static final int PAGE_SIZE = 10;
    private static final int COMMENT_PAGE_SIZE = 10;

    private ImageView ivProduct, ivShopLogo;
    private TextView tvName, tvSubtitle, tvRating, tvPrice, tvOriginalPrice, tvDiscount, tvDescription, tvShopName;
    private RatingBar ratingBar;
    private LinearLayout llAttributesContainer;
    private ImageButton btnGoToCart, btnBuyNow, btnWishlist;
    private Button btnFollow;
    private RecyclerView relatedProductsRecyclerView, commentsRecyclerView;
    private ProgressBar relatedProductsProgressBar, commentsProgressBar;
    private NestedScrollView nestedScrollView;
    private EditText etCommentInput;
    private Button btnPostComment;
    private ApiManager apiManager;
    private ProductAdapter relatedProductAdapter;
    private CommentAdapter commentAdapter;
    private Map<String, String> selectedAttributes = new HashMap<>();
    private String selectedSkuNo;
    private List<Product> relatedProducts = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();
    private List<SpuToSku> skus;
    private boolean isLoadingMore = false;
    private boolean isLoadingMoreComments = false;
    private boolean isInWishlist = false;
    private boolean isShopFollowed = false;
    private List<Object> lastSortValues;
    private boolean isLastPage = false;
    private boolean isLastCommentPage = false;
    private int currentCommentPage = 1;
    private String productThumb;
    private String productId;
    private String shopId;
    private int categoryId;
    private ProductDetail productDetail;
    private int currentUserId = -1; // Khởi tạo mặc định là -1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

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
        relatedProductsRecyclerView = findViewById(R.id.relatedProductsRecyclerView);
        relatedProductsProgressBar = findViewById(R.id.relatedProductsProgressBar);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsProgressBar = findViewById(R.id.commentsProgressBar);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        etCommentInput = findViewById(R.id.etCommentInput);
        btnPostComment = findViewById(R.id.btnPostComment);

        TextView tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

        // Khởi tạo ApiManager
        apiManager = new ApiManager(this);

        // Lấy thông tin người dùng hiện tại
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (accessToken != null) {
            apiManager.getUserProfile(accessToken, new ApiResponseListener<GetUserProfileResponse>() {
                @Override
                public void onSuccess(GetUserProfileResponse response) {
                    if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                        currentUserId = response.getMetadata().getMetadata().getId();
                        commentAdapter = new CommentAdapter(ProductDetailsActivity.this, comments, currentUserId);
                        commentsRecyclerView.setAdapter(commentAdapter);
                    } else {
                        currentUserId = -1;
                        Toast.makeText(ProductDetailsActivity.this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                        finish();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    currentUserId = -1;
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi tải thông tin người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProductDetailsActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } else {
            currentUserId = -1; // Người dùng chưa đăng nhập
            commentAdapter = new CommentAdapter(this, comments, currentUserId);
            commentsRecyclerView.setAdapter(commentAdapter);
        }

        // Cài đặt RecyclerView cho sản phẩm liên quan
        relatedProductsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        relatedProductAdapter = new ProductAdapter(this, relatedProducts, "related");
        relatedProductsRecyclerView.setAdapter(relatedProductAdapter);

        // Cài đặt RecyclerView cho comment
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, comments, currentUserId); // Khởi tạo tạm thời, sẽ cập nhật sau khi lấy userId
        commentsRecyclerView.setAdapter(commentAdapter);

        // Thiết lập OnProductClickListener
        relatedProductAdapter.setOnProductClickListener(product -> {
            String slug = product.getSlug();
            if (slug != null && !slug.isEmpty()) {
                Intent intent = new Intent(ProductDetailsActivity.this, ProductDetailsActivity.class);
                intent.putExtra("product_slug", slug);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(ProductDetailsActivity.this, "Không thể lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        // Sử dụng NestedScrollView để kiểm tra cuộn
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY && !isLoadingMore && !isLastPage) {
                View contentView = nestedScrollView.getChildAt(0);
                int totalHeight = contentView.getHeight();
                int visibleHeight = nestedScrollView.getHeight();
                if (scrollY >= (totalHeight - visibleHeight - 200)) {
                    loadMoreRelatedProducts();
                }
            }
            if (scrollY > oldScrollY && !isLoadingMoreComments && !isLastCommentPage) {
                View contentView = nestedScrollView.getChildAt(0);
                int totalHeight = contentView.getHeight();
                int visibleHeight = nestedScrollView.getHeight();
                if (scrollY >= (totalHeight - visibleHeight - 200)) {
                    loadMoreComments();
                }
            }
        });

        // Thiết lập listener cho comment
        commentAdapter.setOnCommentActionListener(new CommentAdapter.OnCommentActionListener() {
            @Override
            public void onReplyClicked(Comment comment) {
                showReplyDialog(comment);
            }

            @Override
            public void onEditClicked(Comment comment) {
                showEditDialog(comment);
            }

            @Override
            public void onDeleteClicked(Comment comment) {
                deleteComment(comment);
            }

            @Override
            public void onViewRepliesClicked(Comment comment) {
                loadReplies(comment);
            }
        });

        // Sự kiện click
        btnGoToCart.setOnClickListener(v -> showSkuSelectionBottomSheet(false));
        btnBuyNow.setOnClickListener(v -> showSkuSelectionBottomSheet(true));
        btnFollow.setOnClickListener(v -> toggleFollowShop());
        btnWishlist.setOnClickListener(v -> toggleWishlist());
        btnPostComment.setOnClickListener(v -> postComment());

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

    private void loadProductDetail(String slug) {
        relatedProductsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getProductDetail(slug, new ApiResponseListener<ProductDetailResponse>() {
            @Override
            public void onSuccess(ProductDetailResponse response) {
                relatedProductsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    productDetail = response.getMetadata().getMetadata();
                    skus = productDetail.getSpuToSkus();
                    productThumb = productDetail.getThumb();
                    productId = String.valueOf(productDetail.getId());
                    shopId = String.valueOf(productDetail.getShopId());
                    isInWishlist = productDetail.isInWishlist();
                    btnWishlist.setImageResource(isInWishlist ? R.drawable.ic_full_heart : R.drawable.ic_heart_1);
                    categoryId = productDetail.getCategoryId();
                    displayProduct(productDetail);
                    loadShopInfo(shopId);
                    loadRelatedProducts();
                    loadComments();

                    nestedScrollView.smoothScrollTo(0, 0);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                relatedProductsProgressBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải chi tiết sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadComments() {
        commentsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getProductComments(Integer.parseInt(productId), currentCommentPage, COMMENT_PAGE_SIZE, new ApiResponseListener<CommentResponse>() {
            @Override
            public void onSuccess(CommentResponse response) {
                commentsProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<Comment> newComments = response.getMetadata().getMetadata().getComments();
                    if (newComments != null) {
                        comments.addAll(newComments);
                        commentAdapter.notifyDataSetChanged();
                        if (newComments.size() < COMMENT_PAGE_SIZE || comments.size() >= response.getMetadata().getMetadata().getTotalItems()) {
                            isLastCommentPage = true;
                        }
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                commentsProgressBar.setVisibility(View.GONE);
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải bình luận: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreComments() {
        if (!isLoadingMoreComments && !isLastCommentPage) {
            isLoadingMoreComments = true;
            currentCommentPage++;
            loadComments();
        }
    }

    private void loadReplies(Comment comment) {
        apiManager.getCommentReplies(comment.getId(), new ApiResponseListener<RepliesResponse>() {
            @Override
            public void onSuccess(RepliesResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<Comment> replies = response.getMetadata().getMetadata();
                    if (replies != null) {
                        comment.setReplies(replies);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải trả lời: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment() {
        String content = etCommentInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            return;
        }

        apiManager.postComment(Integer.parseInt(productId), content, 5, null, new ApiResponseListener<Comment>() {
            @Override
            public void onSuccess(Comment comment) {
                comments.add(0, comment);
                commentAdapter.notifyItemInserted(0);
                etCommentInput.setText("");
                Toast.makeText(ProductDetailsActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReplyDialog(Comment parentComment) {
        // Tạo AlertDialog để nhập nội dung trả lời
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Trả lời bình luận");

        // Tạo EditText để người dùng nhập nội dung
        final EditText input = new EditText(this);
        input.setHint("Nhập nội dung trả lời...");
        builder.setView(input);

        // Nút "Gửi"
        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String content = input.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(ProductDetailsActivity.this, "Vui lòng nhập nội dung trả lời", Toast.LENGTH_SHORT).show();
                return;
            }

            apiManager.postComment(Integer.parseInt(productId), content, null, parentComment.getId(), new ApiResponseListener<Comment>() {
                @Override
                public void onSuccess(Comment reply) {
                    List<Comment> replies = parentComment.getReplies();
                    if (replies == null) {
                        replies = new ArrayList<>();
                        parentComment.setReplies(replies);
                    }
                    replies.add(reply);
                    commentAdapter.notifyDataSetChanged();
                    Toast.makeText(ProductDetailsActivity.this, "Trả lời thành công", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showEditDialog(Comment comment) {
        // Tạo AlertDialog để chỉnh sửa bình luận
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa bình luận");

        // Tạo EditText để người dùng chỉnh sửa nội dung
        final EditText input = new EditText(this);
        input.setText(comment.getContent()); // Đặt nội dung hiện tại vào EditText
        input.setHint("Nhập nội dung bình luận...");
        builder.setView(input);

        // Nút "Cập nhật"
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newContent = input.getText().toString().trim();
            if (newContent.isEmpty()) {
                Toast.makeText(ProductDetailsActivity.this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
                return;
            }

            apiManager.updateComment(comment.getId(), newContent, new ApiResponseListener<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    comment.setContent(newContent);
                    commentAdapter.notifyDataSetChanged();
                    Toast.makeText(ProductDetailsActivity.this, "Cập nhật bình luận thành công", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteComment(Comment comment) {
        apiManager.deleteComment(comment.getId(), new ApiResponseListener<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                comments.remove(comment);
                commentAdapter.notifyDataSetChanged();
                Toast.makeText(ProductDetailsActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFollowShop() {
        String accessToken = MyPreferences.getString(this, "access_token", null);
        if (accessToken == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để theo dõi shop", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (isShopFollowed) {
            apiManager.unfollowShop(shopId, new ApiResponseListener<FollowShopResponse>() {
                @Override
                public void onSuccess(FollowShopResponse response) {
                    isShopFollowed = false;
                    updateFollowButton();
                    Toast.makeText(ProductDetailsActivity.this, "Đã hủy theo dõi shop", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi hủy theo dõi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiManager.followShop(shopId, new ApiResponseListener<FollowShopResponse>() {
                @Override
                public void onSuccess(FollowShopResponse response) {
                    isShopFollowed = true;
                    updateFollowButton();
                    Toast.makeText(ProductDetailsActivity.this, "Đã theo dõi shop", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ProductDetailsActivity.this, "Lỗi theo dõi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadRelatedProducts() {
        isLoadingMore = true;
        relatedProductsProgressBar.setVisibility(View.VISIBLE);
        apiManager.getRelatedProducts(
                categoryId,
                lastSortValues,
                PAGE_SIZE,
                new ApiResponseListener<SearchProductsResponse>() {
                    @Override
                    public void onSuccess(SearchProductsResponse response) {
                        isLoadingMore = false;
                        relatedProductsProgressBar.setVisibility(View.GONE);
                        if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                            List<SearchProduct> products = response.getMetadata().getMetadata().getData();
                            lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                            int total = response.getMetadata().getMetadata().getTotal();
                            if (products != null && !products.isEmpty()) {
                                relatedProducts.addAll(convertToProducts(products));
                                relatedProductAdapter.notifyDataSetChanged();
                                if (products.size() < PAGE_SIZE || relatedProducts.size() >= total) {
                                    isLastPage = true;
                                }
                            } else {
                                isLastPage = true;
                            }
                        } else {
                            isLastPage = true;
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        isLoadingMore = false;
                        relatedProductsProgressBar.setVisibility(View.GONE);
                        Toast.makeText(ProductDetailsActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadMoreRelatedProducts() {
        if (!isLoadingMore && !isLastPage && categoryId != 0) {
            isLoadingMore = true;
            relatedProductsProgressBar.setVisibility(View.VISIBLE);
            apiManager.getRelatedProducts(
                    categoryId,
                    lastSortValues,
                    PAGE_SIZE,
                    new ApiResponseListener<SearchProductsResponse>() {
                        @Override
                        public void onSuccess(SearchProductsResponse response) {
                            isLoadingMore = false;
                            relatedProductsProgressBar.setVisibility(View.GONE);
                            if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                                List<SearchProduct> products = response.getMetadata().getMetadata().getData();
                                lastSortValues = response.getMetadata().getMetadata().getLastSortValues();
                                int total = response.getMetadata().getMetadata().getTotal();
                                if (products != null && !products.isEmpty()) {
                                    relatedProducts.addAll(convertToProducts(products));
                                    relatedProductAdapter.notifyItemRangeInserted(relatedProducts.size() - products.size(), products.size());
                                    if (products.size() < PAGE_SIZE || relatedProducts.size() >= total) {
                                        isLastPage = true;
                                    }
                                } else {
                                    isLastPage = true;
                                }
                            } else {
                                isLastPage = true;
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            isLoadingMore = false;
                            relatedProductsProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ProductDetailsActivity.this, "Lỗi tải thêm sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    private List<Product> convertToProducts(List<SearchProduct> searchProducts) {
        List<Product> products = new ArrayList<>();
        for (SearchProduct sp : searchProducts) {
            products.add(sp.toProduct());
        }
        return products;
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

        Glide.with(this)
                .load(productThumb)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivProductThumb);

        tvTitle.setText(isBuyNow ? "Chọn biến thể để mua ngay" : "Chọn biến thể để thêm vào giỏ");
        btnConfirm.setText(isBuyNow ? "Mua ngay" : "Thêm vào giỏ");

        chipGroupSkus.removeAllViews();
        for (SpuToSku spuToSku : skus) {
            Sku sku = spuToSku.getSku();
            if ("active".equals(sku.getStatus())) {
                String displayText = formatSkuDisplay(sku);
                Chip chip = new Chip(this);
                chip.setText(displayText);
                chip.setTextSize(14);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.chip_background);
                chip.setChipStrokeWidth(2);
                chip.setChipStrokeColorResource(R.color.chip_stroke);
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
            if (accessToken == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                bottomSheetDialog.dismiss();
                return;
            }

            if (isBuyNow) {
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

        Glide.with(this)
                .load(productThumb)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(ivProduct);

        ivShopLogo.setImageResource(R.drawable.ic_placeholder);

        llAttributesContainer.removeAllViews();
        Map<String, Object> attrs = productDetail.getAttrs();
        if (attrs != null && !attrs.isEmpty()) {
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                TextView title = new TextView(this);
                title.setText(capitalize(key));
                title.setTextSize(16);
                title.setTextColor(getResources().getColor(R.color.black));
                title.setTypeface(null, android.graphics.Typeface.BOLD);
                title.setPadding(0, 16, 0, 8);
                llAttributesContainer.addView(title);

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

    private void loadShopInfo(String shopId) {
        apiManager.getShopInfo(shopId, new ApiResponseListener<ShopInfoResponse>() {
            @Override
            public void onSuccess(ShopInfoResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    ShopInfoResponse.ShopMetadata metadata = response.getMetadata().getMetadata();
                    isShopFollowed = metadata.isFollowing();
                    tvShopName.setText(metadata.getShop().getName());
                    Glide.with(ProductDetailsActivity.this)
                            .load(metadata.getShop().getLogo())
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
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
                Toast.makeText(ProductDetailsActivity.this, "Lỗi tải thông tin shop: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFollowButton() {
        Log.d(TAG, "Updating follow button, isShopFollowed: " + isShopFollowed);
        btnFollow.setText(isShopFollowed ? "Unfollow" : "Follow");
        btnFollow.setSelected(isShopFollowed);
        btnFollow.setTextColor(isShopFollowed ? getResources().getColor(android.R.color.black, null) : getResources().getColor(android.R.color.white, null));
        btnFollow.invalidate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String slug = intent.getStringExtra("product_slug");
        if (slug != null && !slug.isEmpty()) {
            relatedProducts.clear();
            comments.clear();
            relatedProductAdapter.notifyDataSetChanged();
            commentAdapter.notifyDataSetChanged();
            isLastPage = false;
            isLastCommentPage = false;
            currentCommentPage = 1;
            lastSortValues = null;
            loadProductDetail(slug);
        }
    }
}