package com.example.shopman.remote;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.shopman.models.ErrorResponse;
import com.example.shopman.models.FcmTokenRequest;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.changepassword.request.ChangePasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordResponse;
import com.example.shopman.models.changepassword.response.ChangePasswordResponse;
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
import com.example.shopman.utilitis.MyPreferences;
import com.google.gson.Gson;

import java.io.IOException;

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
        Log.d(TAG, "Login Request: email=" + email);
        LoginRequest request = new LoginRequest(email, password);
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
        FcmTokenRequest request = new FcmTokenRequest(fcmToken);
        Call<Void> call = apiService.updateFcmToken("Bearer " + accessToken, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Update FCM Token Response: HTTP " + response.code());
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else {
                    listener.onError("Failed to update FCM token: " + getErrorMessage(response));
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
                Log.d(TAG, "Get Product Detail Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to fetch product detail: " + getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                Log.e(TAG, "Get Product Detail Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }
}