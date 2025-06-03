package com.example.shopman.remote;

import com.example.shopman.models.Banner.BannerResponse;
import com.example.shopman.models.Campaign.CampaignProductsResponse;
import com.example.shopman.models.Campaign.CampaignResponse;
import com.example.shopman.models.DealofTheDay.DealProductResponse;
import com.example.shopman.models.FcmTokenRequest;
import com.example.shopman.models.NewArrivals.NewArrivalsResponse;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.Shop.ShopProductsResponse;
import com.example.shopman.models.ShopResponse;
import com.example.shopman.models.TopTrendingProducts.TrendingProductResponse;
import com.example.shopman.models.auth.RefreshTokenResponse;
import com.example.shopman.models.cart.CartAddRequest;
import com.example.shopman.models.cart.CartAddResponse;
import com.example.shopman.models.cart.CartResponse;
import com.example.shopman.models.category.CategoryProductResponse;
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
import com.example.shopman.models.wishlist.WishlistRequest;
import com.example.shopman.models.wishlist.WishlistResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
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
}