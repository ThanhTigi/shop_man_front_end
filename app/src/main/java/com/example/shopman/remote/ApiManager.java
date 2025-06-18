package com.example.shopman.remote;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

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
import com.example.shopman.models.ErrorResponse;
import com.example.shopman.models.FcmTokenRequest;
import com.example.shopman.models.NewArrivals.NewArrivalsResponse;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.Shop.FollowShopResponse;
import com.example.shopman.models.Shop.ShopInfoResponse;
import com.example.shopman.models.Shop.ShopProductsResponse;
import com.example.shopman.models.ShopResponse;
import com.example.shopman.models.TopTrendingProducts.TrendingProductResponse;
import com.example.shopman.models.cart.CartAddRequest;
import com.example.shopman.models.cart.CartResponse;
import com.example.shopman.models.category.CategoryResponse;
import com.example.shopman.models.changepassword.request.ChangePasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.response.ForgotPasswordResponse;
import com.example.shopman.models.changepassword.response.ChangePasswordResponse;
import com.example.shopman.models.checkotp.CheckOTPResponse;
import com.example.shopman.models.login.GoogleLoginRequest;
import com.example.shopman.models.login.LoginRequest;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.searchproducts.SearchProductsResponse;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.models.cart.CartAddResponse;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.wishlist.Add.WishlistRequest;
import com.example.shopman.models.wishlist.Add.WishlistResponse;
import com.example.shopman.models.wishlist.Remove.WishlistRemoveResponse;
import com.example.shopman.utilitis.MyPreferences;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {
    private static final String TAG = "ApiManager";
    private final ApiService apiService;
    private final Context context;

    public ApiManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Lưu dữ liệu xác thực (token và thông tin người dùng)
    // Lưu dữ liệu xác thực (token và thông tin người dùng)update
    private void saveAuthData(String accessToken, String refreshToken, Object user) throws Exception {
        try {
            MyPreferences.setString(context, "access_token", accessToken);
            MyPreferences.setString(context, "refresh_token", refreshToken);
            MyPreferences.setString(context, "current_user_meta_data", new Gson().toJson(user));
            Log.d(TAG, "Saved auth data successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to save auth data: " + e.getMessage());
            throw e;
        }
    }

    // Xử lý lỗi phản hồi từ API
    private String getErrorMessage(Response<?> response) {
        String errorMsg = response.message();
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null) {
                    errorMsg = errorResponse.getMessage();
                } else {
                    errorMsg = errorBody;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error parsing error body: " + e.getMessage());
        }
        return errorMsg;
    }

    public void login(String email, String password, final ApiResponseListener<LoginResponse> listener) {
        Log.d(TAG, "Login Request: email=" + email + " password="+ password);
        LoginRequest request = new LoginRequest(email, password);
        Log.d(TAG, "Login Request Body: " + new Gson().toJson(request));
        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Login Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    try {
                        saveAuthData(
                                loginResponse.getMetadata().getMetadata().getTokens().getAccessToken(),
                                loginResponse.getMetadata().getMetadata().getTokens().getRefreshToken(),
                                loginResponse.getMetadata().getMetadata().getUser()
                        );
                        listener.onSuccess(loginResponse);
                    } catch (Exception e) {
                        listener.onError("Failed to save login data: " + e.getMessage());
                    }
                } else {
                    listener.onError("Login failed: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void loginWithGoogle(String idToken, final ApiResponseListener<LoginResponse> listener) {
        Log.d(TAG, "Google Login Request: idToken=" + idToken);
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<LoginResponse> call = apiService.loginWithGoogle(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Google Login Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    try {
                        saveAuthData(
                                loginResponse.getMetadata().getMetadata().getTokens().getAccessToken(),
                                loginResponse.getMetadata().getMetadata().getTokens().getRefreshToken(),
                                loginResponse.getMetadata().getMetadata().getUser()
                        );
                        listener.onSuccess(loginResponse);
                    } catch (Exception e) {
                        listener.onError("Failed to save Google login data: " + e.getMessage());
                    }
                } else {
                    listener.onError("Google login failed: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Google Login Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void signUp(SignUpRequest request, ApiResponseListener<SignUpResponse> listener) {
        Log.d(TAG, "Sign Up Request: " + new Gson().toJson(request));
        Call<SignUpResponse> call = apiService.signUp(request);

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Log.d(TAG, "Sign Up Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    SignUpResponse signUpResponse = response.body();
                    try {
                        saveAuthData(
                                signUpResponse.getMetaData().getMetadata().getTokens().getAccessToken(),
                                signUpResponse.getMetaData().getMetadata().getTokens().getRefreshToken(),
                                signUpResponse.getMetaData().getMetadata().getUser()
                        );
                        listener.onSuccess(signUpResponse);
                    } catch (Exception e) {
                        listener.onError("Failed to save sign-up data: " + e.getMessage());
                    }
                } else {
                    listener.onError("Sign up failed: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e(TAG, "Sign Up Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void changePassword(ChangePasswordRequest request, ApiResponseListener<ChangePasswordResponse> listener) {
        Log.d(TAG, "Change Password Request: " + new Gson().toJson(request));
        Call<ChangePasswordResponse> call = apiService.changePassword(request);

        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                Log.d(TAG, "Change Password Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    ChangePasswordResponse changePasswordResponse = response.body();
                    try {
                        saveAuthData(
                                changePasswordResponse.getMetaData().getMetadata().getTokens().getAccessToken(),
                                changePasswordResponse.getMetaData().getMetadata().getTokens().getRefreshToken(),
                                changePasswordResponse.getMetaData().getMetadata().getUser()
                        );
                        listener.onSuccess(changePasswordResponse);
                    } catch (Exception e) {
                        listener.onError("Failed to save password change data: " + e.getMessage());
                    }
                } else {
                    listener.onError("Failed to change password: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.e(TAG, "Change Password Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void forgotPassword(ForgotPasswordRequest request, ApiResponseListener<ForgotPasswordResponse> listener) {
        Log.d(TAG, "Forgot Password Request: " + new Gson().toJson(request));
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(request);

        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                Log.d(TAG, "Forgot Password Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to send OTP: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Log.e(TAG, "Forgot Password Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void checkOTP(OTPRequest request, ApiResponseListener<CheckOTPResponse> listener) {
        Log.d(TAG, "Check OTP Request: " + new Gson().toJson(request));
        Call<CheckOTPResponse> call = apiService.checkOTP(request);

        call.enqueue(new Callback<CheckOTPResponse>() {
            @Override
            public void onResponse(Call<CheckOTPResponse> call, Response<CheckOTPResponse> response) {
                Log.d(TAG, "Check OTP Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to verify OTP: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<CheckOTPResponse> call, Throwable t) {
                Log.e(TAG, "Check OTP Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getUserProfile(String accessToken, ApiResponseListener<GetUserProfileResponse> listener) {
        Log.d(TAG, "Get User Profile Request: accessToken=" + accessToken);
        Call<GetUserProfileResponse> call = apiService.getUserProfile("Bearer " + accessToken);

        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                Log.d(TAG, "Get User Profile Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to fetch profile: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Get User Profile Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void updateUserProfile(String accessToken, UpdateProfileRequest request, ApiResponseListener<GetUserProfileResponse> listener) {
        Log.d(TAG, "Update User Profile Request: " + new Gson().toJson(request));
        Call<GetUserProfileResponse> call = apiService.updateUserProfile("Bearer " + accessToken, request);

        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                Log.d(TAG, "Update User Profile Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to update profile: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                Log.e(TAG, "Update User Profile Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void updateFcmToken(String accessToken, String fcmToken, ApiResponseListener<Void> listener) {
        Log.d(TAG, "Update FCM Token Request: fcmToken=" + fcmToken);
        String refreshToken = MyPreferences.getString(context, "refresh_token", null);
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(refreshToken)) {
            Log.e(TAG, "Invalid tokens: access_token=" + accessToken + ", refresh_token=" + refreshToken);
            listener.onError("Session expired. Please log in again.");
            Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
            context.sendBroadcast(intent);
            return;
        }
        FcmTokenRequest request = new FcmTokenRequest(fcmToken);
        Call<Void> call = apiService.updateFcmToken("Bearer " + accessToken, request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Update FCM Token Response: HTTP " + response.code());
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to update FCM token: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Update FCM Token Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void searchProducts(String query, String lastSortValues, int pageSize, ApiResponseListener<SearchProductsResponse> listener) {
        Log.d(TAG, "Search Products Request: query=" + query + ", lastSortValues=" + lastSortValues);
        Call<SearchProductsResponse> call = apiService.searchProducts(query, lastSortValues, pageSize);

        call.enqueue(new Callback<SearchProductsResponse>() {
            @Override
            public void onResponse(Call<SearchProductsResponse> call, Response<SearchProductsResponse> response) {
                Log.d(TAG, "Search Products Response: HTTP " + response.code() + ", body=" + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Search failed: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SearchProductsResponse> call, Throwable t) {
                Log.e(TAG, "Search Products Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getProductDetail(String slug, ApiResponseListener<ProductDetailResponse> listener) {
        Log.d(TAG, "Get Product Detail Request: slug=" + slug);
        Call<ProductDetailResponse> call = apiService.getProductDetail(slug);
        call.enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                Log.d(TAG, "onResponse: HTTP " + response.code() + ", raw: " + response);
                if (response.body() != null) {
                    Log.d(TAG, "Get Product Detail Response: " + new Gson().toJson(response.body()));
                } else {
                    Log.w(TAG, "Response body is null, code: " + response.code());
                }
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    Log.e(TAG, "Error fetching product detail: " + errorMessage);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to fetch product detail: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Log.e(TAG, "Get Product Detail Error: " + t.getMessage(), t);
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void addToWishlist(String accessToken, int productId, ApiResponseListener<WishlistResponse> listener) {
        Log.d(TAG, "Add to Wishlist Request: productId=" + productId);
        WishlistRequest request = new WishlistRequest(productId);
        Call<WishlistResponse> call = apiService.addToWishlist("Bearer " + accessToken, request);

        call.enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                Log.d(TAG, "Add to Wishlist Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to add to wishlist: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                Log.e(TAG, "Add to Wishlist Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void addToCart(String accessToken, String productId, String skuNo, int quantity, ApiResponseListener<CartAddResponse> listener) {
        Log.d(TAG, "Add to Cart Request: productId=" + productId + ", skuNo=" + skuNo + ", quantity=" + quantity);
        CartAddRequest request = new CartAddRequest(productId, skuNo, quantity);
        Call<CartAddResponse> call = apiService.addToCart("Bearer " + accessToken, request);

        call.enqueue(new Callback<CartAddResponse>() {
            @Override
            public void onResponse(Call<CartAddResponse> call, Response<CartAddResponse> response) {
                Log.d(TAG, "Add to Cart Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to add to cart: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<CartAddResponse> call, Throwable t) {
                Log.e(TAG, "Add to Cart Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getCart(ApiResponseListener<CartResponse> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e(TAG, "Get Cart Error: No access token found");
            listener.onError("Please log in to view your cart");
            Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
            context.sendBroadcast(intent);
            return;
        }

        Log.d(TAG, "Get Cart Request: accessToken=Bearer " + accessToken);
        Call<CartResponse> call = apiService.getCart("Bearer " + accessToken);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d(TAG, "Get Cart Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to fetch cart: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                Log.e(TAG, "Get Cart Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getCategories(ApiResponseListener<CategoryResponse> listener) {
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                Log.d(TAG, "Get Categories Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to fetch categories: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e(TAG, "Get Categories Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getTrendingProducts(float cursor, int limit, ApiResponseListener<TrendingProductResponse> listener) {
        Call<TrendingProductResponse> call = apiService.getTrendingProducts(cursor, limit);
        call.enqueue(new Callback<TrendingProductResponse>() {
            @Override
            public void onResponse(Call<TrendingProductResponse> call, Response<TrendingProductResponse> response) {
                Log.d(TAG, "Get Trending Products Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to fetch trending products: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<TrendingProductResponse> call, Throwable t) {
                Log.e(TAG, "Get Trending Products Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getCategoryProducts(String slug, String lastSortValues, Integer pageSize, ApiResponseListener<SearchProductsResponse> listener) {
        Log.d(TAG, "Get Category Products Request: slug=" + slug + ", lastSortValues=" + lastSortValues + ", pageSize=" + (pageSize != null ? pageSize : "default"));
        Call<SearchProductsResponse> call = apiService.getCategoryProducts(slug, lastSortValues, pageSize);

        call.enqueue(new Callback<SearchProductsResponse>() {
            @Override
            public void onResponse(Call<SearchProductsResponse> call, Response<SearchProductsResponse> response) {
                Log.d(TAG, "Get Category Products Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to fetch category products: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<SearchProductsResponse> call, Throwable t) {
                Log.e(TAG, "Get Category Products Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getBanners(ApiResponseListener<BannerResponse> listener) {
        Log.d(TAG, "Calling getBanners: /banners");
        Call<BannerResponse> call = apiService.getBanners();
        call.enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                Log.d(TAG, "Banners Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Lỗi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                Log.e(TAG, "Banners Failure: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getCampaignDetails(String slug, ApiResponseListener<CampaignResponse> listener) {
        Log.d(TAG, "Calling getCampaignDetails: /campaign/" + slug);
        Call<CampaignResponse> call = apiService.getCampaignDetails(slug);
        call.enqueue(new Callback<CampaignResponse>() {
            @Override
            public void onResponse(Call<CampaignResponse> call, Response<CampaignResponse> response) {
                Log.d(TAG, "Campaign Details Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<CampaignResponse> call, Throwable t) {
                Log.e(TAG, "Campaign Details Failure: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getCampaignProducts(String slug, int page, int limit, ApiResponseListener<CampaignProductsResponse> listener) {
        Log.d(TAG, "Calling getCampaignProducts: /campaign/" + slug + "/product?page=" + page + "&limit=" + limit);
        Call<CampaignProductsResponse> call = apiService.getCampaignProducts(slug, page, limit);
        call.enqueue(new Callback<CampaignProductsResponse>() {
            @Override
            public void onResponse(Call<CampaignProductsResponse> call, Response<CampaignProductsResponse> response) {
                Log.d(TAG, "Campaign Products Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<CampaignProductsResponse> call, Throwable t) {
                Log.e(TAG, "Campaign Products Failure: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getShopDetails(String slug, ApiResponseListener<ShopResponse> listener) {
        Log.d(TAG, "Calling getShopDetails: /shop/" + slug);
        Call<ShopResponse> call = apiService.getShopDetails(slug);
        call.enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                Log.d(TAG, "Shop Details Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Lỗi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                Log.e(TAG, "Shop Details Failure: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getShopProducts(String slug, List<Object> lastSortValues, ApiResponseListener<ShopProductsResponse> listener) {
        Log.d(TAG, "Calling getShopProducts: /shop/" + slug + "/product");
        Call<ShopProductsResponse> call = apiService.getShopProducts(slug, lastSortValues);
        call.enqueue(new Callback<ShopProductsResponse>() {
            @Override
            public void onResponse(Call<ShopProductsResponse> call, Response<ShopProductsResponse> response) {
                Log.d(TAG, "Shop Products Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Lỗi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ShopProductsResponse> call, Throwable t) {
                Log.e(TAG, "Shop Products Failure: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getWishlist(String accessToken, int page, int limit, ApiResponseListener<WishlistResponse> listener) {
        Log.d(TAG, "Get Wishlist Request: page=" + page + ", limit=" + limit);
        Call<WishlistResponse> call = apiService.getWishlist("Bearer " + accessToken, page, limit);
        call.enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                Log.d(TAG, "Get Wishlist Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMessage = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError("Failed to fetch wishlist: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                Log.e(TAG, "Get Wishlist Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getDealProducts(int page, int limit, ApiResponseListener<DealProductResponse> listener) {
        Log.d(TAG, "Get Deal Products Request: page=" + page + ", limit=" + limit);
        Call<DealProductResponse> call = apiService.getDealProducts(page, limit);
        call.enqueue(new Callback<DealProductResponse>() {
            @Override
            public void onResponse(Call<DealProductResponse> call, Response<DealProductResponse> response) {
                Log.d(TAG, "Get Deal Products Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Lỗi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DealProductResponse> call, Throwable t) {
                Log.e(TAG, "Get Deal Products Error: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getNewArrivals(int page, Integer pageSize, ApiResponseListener<NewArrivalsResponse> listener) {
        Log.d(TAG, "Get New Arrivals Request: page=" + page + ", pageSize=" + (pageSize != null ? pageSize : "default"));
        Call<NewArrivalsResponse> call = apiService.getNewArrivals(page, pageSize);
        call.enqueue(new Callback<NewArrivalsResponse>() {
            @Override
            public void onResponse(Call<NewArrivalsResponse> call, Response<NewArrivalsResponse> response) {
                Log.d(TAG, "Get New Arrivals Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Lỗi API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NewArrivalsResponse> call, Throwable t) {
                Log.e(TAG, "Get New Arrivals Error: " + t.getMessage());
                listener.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void removeFromWishlist(String accessToken, int productId, ApiResponseListener<WishlistRemoveResponse> listener) {
        String authHeader = "Bearer " + accessToken;
        Call<WishlistRemoveResponse> call = apiService.removeFromWishlist(authHeader, productId);
        call.enqueue(new Callback<WishlistRemoveResponse>() {
            @Override
            public void onResponse(Call<WishlistRemoveResponse> call, Response<WishlistRemoveResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<WishlistRemoveResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }
    public void getShopInfo(String shopId, ApiResponseListener<ShopInfoResponse> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        String authHeader = accessToken != null ? "Bearer " + accessToken : "";
        Call<ShopInfoResponse> call = apiService.getShopInfo(authHeader, shopId);
        call.enqueue(new Callback<ShopInfoResponse>() {
            @Override
            public void onResponse(Call<ShopInfoResponse> call, Response<ShopInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ShopInfoResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }

    public void followShop(String shopId, ApiResponseListener<FollowShopResponse> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        if (accessToken == null) {
            listener.onError("Vui lòng đăng nhập");
            return;
        }
        String authHeader = "Bearer " + accessToken;
        Call<FollowShopResponse> call = apiService.followShop(authHeader, shopId);
        call.enqueue(new Callback<FollowShopResponse>() {
            @Override
            public void onResponse(Call<FollowShopResponse> call, Response<FollowShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<FollowShopResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }

    public void unfollowShop(String shopId, ApiResponseListener<FollowShopResponse> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        if (accessToken == null) {
            listener.onError("Vui lòng đăng nhập");
            return;
        }
        String authHeader = "Bearer " + accessToken;
        Call<FollowShopResponse> call = apiService.unfollowShop(authHeader, shopId);
        call.enqueue(new Callback<FollowShopResponse>() {
            @Override
            public void onResponse(Call<FollowShopResponse> call, Response<FollowShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<FollowShopResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }
    public void getRelatedProducts(
            int categoryId,
            List<Object> lastSortValues,
            int pageSize,
            ApiResponseListener<SearchProductsResponse> listener
    ) {
        String lastSortValuesJson = lastSortValues != null ? new Gson().toJson(lastSortValues) : null;
        Log.d(TAG, "Requesting related products for categoryId: " + categoryId +
                ", lastSortValues: " + lastSortValuesJson);

        Call<SearchProductsResponse> call = apiService.getRelatedProducts(
                categoryId,
                lastSortValuesJson,
                pageSize
        );

        call.enqueue(new Callback<SearchProductsResponse>() {
            @Override
            public void onResponse(Call<SearchProductsResponse> call, Response<SearchProductsResponse> response) {
                Log.d(TAG, "onResponse: "+response);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Related products response: " + new Gson().toJson(response.body()));
                    listener.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed to load related products: " + response.message());
                    listener.onError("Failed to load related products: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SearchProductsResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load related products: " + t.getMessage());
                listener.onError("Failed to load related products: " + t.getMessage());
            }
        });
    }

    public void updateComment(int commentId, String content, Integer rating, List<String> imageUrls, ApiResponseListener<Comment> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        if (accessToken == null) {
            listener.onError("Vui lòng đăng nhập");
            return;
        }
        String authHeader = "Bearer " + accessToken;
        UpdateCommentRequest request = new UpdateCommentRequest(content, rating, imageUrls);
        Call<CommentCreateResponse> call = apiService.updateComment(authHeader, commentId, request);
        call.enqueue(new Callback<CommentCreateResponse>() {
            @Override
            public void onResponse(Call<CommentCreateResponse> call, Response<CommentCreateResponse> response) {
                Log.d(TAG, "Update Comment Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    Comment updatedComment = response.body().getMetadata().getMetadata();
                    if (updatedComment != null) {
                        listener.onSuccess(updatedComment);
                    } else {
                        listener.onError("Không tìm thấy comment trong phản hồi");
                    }
                } else {
                    String errorMsg = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMsg = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<CommentCreateResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }

    public void postComment(int productId, String content, Integer rating, Integer parentId, List<String> imageUrls, ApiResponseListener<Comment> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        if (accessToken == null) {
            listener.onError("Vui lòng đăng nhập");
            return;
        }
        String authHeader = "Bearer " + accessToken;
        PostCommentRequest request = new PostCommentRequest(content, rating, parentId, imageUrls);
        Call<CommentCreateResponse> call = apiService.postComment(authHeader, productId, request);
        call.enqueue(new Callback<CommentCreateResponse>() {
            @Override
            public void onResponse(Call<CommentCreateResponse> call, Response<CommentCreateResponse> response) {
                Log.d(TAG, "Post Comment Response: HTTP " + response.code() + ", body=" +
                        (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    Comment newComment = response.body().getMetadata().getMetadata();
                    if (newComment != null) {
                        listener.onSuccess(newComment);
                    } else {
                        listener.onError("Không tìm thấy comment trong phản hồi");
                    }
                } else {
                    String errorMsg = getErrorMessage(response);
                    if (response.code() == 401) {
                        errorMsg = "Session expired. Please log in again.";
                        Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                        context.sendBroadcast(intent);
                    }
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<CommentCreateResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }



    public void getProductComments(int productId, int limit, String nextCursor, ApiResponseListener<CommentResponse> listener) {
        String accessToken = MyPreferences.getString(context, "access_token", null);
        String authHeader = TextUtils.isEmpty(accessToken) ? "" : "Bearer " + accessToken;
        Call<CommentResponse> call = apiService.getProductComments(authHeader, productId, limit, nextCursor);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful()) {
                    CommentResponse body = response.body();
                    if (body != null) {
                        listener.onSuccess(body);
                    } else {
                        listener.onError("Phản hồi trống từ server");
                    }
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        } else {
                            errorMsg += " - Không có chi tiết";
                        }
                    } catch (IOException e) {
                        errorMsg += " - Không thể đọc chi tiết lỗi";
                        Log.e(TAG, "Lỗi đọc errorBody", e);
                    }
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }

    public void getCommentReplies(int commentId, ApiResponseListener<RepliesResponse> listener) {
        Call<RepliesResponse> call = apiService.getCommentReplies("", commentId); // Để header rỗng, Interceptor sẽ xử lý
        call.enqueue(new Callback<RepliesResponse>() {
            @Override
            public void onResponse(Call<RepliesResponse> call, Response<RepliesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }
            @Override
            public void onFailure(Call<RepliesResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }
    public void deleteComment(int commentId, ApiResponseListener<DeleteCommentResponse> listener) {
        Call<DeleteCommentResponse> call = apiService.deleteComment("", commentId); // Interceptor sẽ xử lý token
        call.enqueue(new retrofit2.Callback<DeleteCommentResponse>() {
            @Override
            public void onResponse(Call<DeleteCommentResponse> call, Response<DeleteCommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi server: " + response.code() + " - " + (response.errorBody() != null ? response.errorBody().toString() : "Không có chi tiết");
                    Log.e(TAG, errorMsg);
                    listener.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<DeleteCommentResponse> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                listener.onError(errorMsg);
            }
        });
    }
}