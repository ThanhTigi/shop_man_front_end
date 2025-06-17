package com.example.shopman.remote;

import com.example.shopman.models.Banner.BannerResponse;
import com.example.shopman.models.Campaign.CampaignProductsResponse;
import com.example.shopman.models.Campaign.CampaignResponse;
import com.example.shopman.models.Comments.Comment;
import com.example.shopman.models.Comments.CommentCreateResponse;
import com.example.shopman.models.Comments.CommentResponse;
import com.example.shopman.models.Comments.DeleteCommentResponse;
import com.example.shopman.models.Comments.PostCommentRequest;
import com.example.shopman.models.Comments.RepliesResponse;
import com.example.shopman.models.Comments.UpdateCommentRequest;
import com.example.shopman.models.DealofTheDay.DealProductResponse;
import com.example.shopman.models.FcmTokenRequest;
import com.example.shopman.models.NewArrivals.NewArrivalsResponse;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.Shop.FollowShopResponse;
import com.example.shopman.models.Shop.ShopInfoResponse;
import com.example.shopman.models.Shop.ShopProductsResponse;
import com.example.shopman.models.ShopResponse;
import com.example.shopman.models.TopTrendingProducts.TrendingProductResponse;
import com.example.shopman.models.auth.RefreshTokenResponse;
import com.example.shopman.models.cart.CartAddRequest;
import com.example.shopman.models.cart.CartAddResponse;
import com.example.shopman.models.cart.CartResponse;
import com.example.shopman.models.category.CategoryResponse;
import com.example.shopman.models.changepassword.request.ChangePasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.response.ChangePasswordResponse;
import com.example.shopman.models.changepassword.response.ForgotPasswordResponse;
import com.example.shopman.models.checkotp.CheckOTPResponse;
import com.example.shopman.models.login.GoogleLoginRequest;
import com.example.shopman.models.login.LoginRequest;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.wishlist.Add.WishlistRequest;
import com.example.shopman.models.wishlist.Add.WishlistResponse;
import com.example.shopman.models.wishlist.Remove.WishlistRemoveResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/v1/auth/login-with-google")
    Call<LoginResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("/api/v1/auth/signup")
    Call<SignUpResponse> signUp(@Body SignUpRequest request);

    @POST("/api/v1/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/api/v1/auth/check-otp")
    Call<CheckOTPResponse> checkOTP(@Body OTPRequest request);

    @POST("/api/v1/auth/change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("/api/v1/auth/handle-refreshtoken")
    Call<RefreshTokenResponse> refreshToken(@Header("x-rtoken-id") String refreshToken);

    @GET("/api/v1/user/profile")
    Call<GetUserProfileResponse> getUserProfile(@Header("Authorization") String authorization);

    @PUT("/api/v1/user/profile/update")
    Call<GetUserProfileResponse> updateUserProfile(@Header("Authorization") String authorization, @Body UpdateProfileRequest request);

    @DELETE("/api/v1/wishlist/{productId}")
    Call<WishlistRemoveResponse> removeFromWishlist(
            @Header("Authorization") String authorization,
            @Path("productId") int productId
    );

    @GET("/api/v1/product/search")
    Call<SearchProductsResponse> searchProducts(
            @Query("query") String query,
            @Query("lastSortValues") String lastSortValues,
            @Query("pageSize") int pageSize
    );

    @POST("/api/v1/auth/update-fcm-token")
    Call<Void> updateFcmToken(@Header("Authorization") String authorization, @Body FcmTokenRequest request);

    @GET("/api/v1/product/detail/{slug}")
    Call<ProductDetailResponse> getProductDetail(@Path("slug") String slug);

    @POST("/api/v1/wishlist")
    Call<WishlistResponse> addToWishlist(@Header("Authorization") String authorization, @Body WishlistRequest request);

    @POST("/api/v1/cart/add")
    Call<CartAddResponse> addToCart(@Header("Authorization") String authorization, @Body CartAddRequest request);

    @GET("/api/v1/cart")
    Call<CartResponse> getCart(@Header("Authorization") String authorization);

    @GET("/api/v1/category")
    Call<CategoryResponse> getCategories();

    @GET("/api/v1/category/{slug}/product")
    Call<SearchProductsResponse> getCategoryProducts(
            @Path("slug") String slug,
            @Query("lastSortValues") String lastSortValues,
            @Query("pageSize") Integer pageSize
    );

    @GET("/api/v1/banner")
    Call<BannerResponse> getBanners();

    @GET("/api/v1/campaign/{slug}")
    Call<CampaignResponse> getCampaignDetails(@Path("slug") String slug);

    @GET("/api/v1/campaign/{slug}/product")
    Call<CampaignProductsResponse> getCampaignProducts(
            @Path("slug") String slug,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/api/v1/shop/{slug}")
    Call<ShopResponse> getShopDetails(@Path("slug") String slug);

    @GET("/api/v1/shop/{slug}/product")
    Call<ShopProductsResponse> getShopProducts(
            @Path("slug") String slug,
            @Query("lastSortValues") List<Object> lastSortValues
    );

    @GET("/api/v1/wishlist")
    Call<WishlistResponse> getWishlist(
            @Header("Authorization") String authorization,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/api/v1/product/all-trending-products")
    Call<TrendingProductResponse> getTrendingProducts(
            @Query("cursor") float cursor,
            @Query("limit") int limit
    );

    @GET("/api/v1/product/deal-of-the-day")
    Call<DealProductResponse> getDealProducts(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("/api/v1/new-arrivals")
    Call<NewArrivalsResponse> getNewArrivals(
            @Query("page") int page,
            @Query("pageSize") Integer pageSize
    );
    @GET("/api/v1/shop/info/{shopId}")
    Call<ShopInfoResponse> getShopInfo(
            @Header("Authorization") String authorization,
            @Path("shopId") String shopId
    );

    @POST("/api/v1/shop/{shopId}/follow")
    Call<FollowShopResponse> followShop(
            @Header("Authorization") String authorization,
            @Path("shopId") String shopId
    );

    @DELETE("/api/v1/shop/{shopId}/follow")
    Call<FollowShopResponse> unfollowShop(
            @Header("Authorization") String authorization,
            @Path("shopId") String shopId
    );
    @GET("/api/v1/category/{categoryId}/related-product")
    Call<SearchProductsResponse> getRelatedProducts(
            @Path("categoryId") int categoryId,
            @Query("lastSortValues") String lastSortValues,
            @Query("pageSize") int pageSize
    );
    @POST("/api/v1/product/{id}/comments")
    Call<CommentCreateResponse> postComment(
            @Header("Authorization") String authHeader,
            @Path("id") int productId,
            @Body PostCommentRequest request
    );
    @GET("/api/v1/product/{id}/comments")
    Call<CommentResponse> getProductComments(
            @Header("Authorization") String authorization,
            @Path("id") int productId,
            @Query("limit") int limit,
            @Query("cursor") String cursor
    );

    @GET("/api/v1/comment/{id}/replies")
    Call<RepliesResponse> getCommentReplies(
            @Header("Authorization") String authorization,
            @Path("id") int commentId
    );

    @PUT("/api/v1/comment/{id}")
    Call<CommentCreateResponse> updateComment(
            @Header("Authorization") String authorization,
            @Path("id") int commentId,
            @Body UpdateCommentRequest request
    );

    @DELETE("/api/v1/comment/{id}")
    Call<DeleteCommentResponse> deleteComment(
            @Header("Authorization") String authorization,
            @Path("id") int commentId
    );
}