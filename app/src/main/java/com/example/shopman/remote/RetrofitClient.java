package com.example.shopman.remote;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.shopman.models.auth.InnerMetadata;
import com.example.shopman.models.auth.RefreshTokenResponse;
import com.example.shopman.utilitis.MyPreferences;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://shopman.onrender.com/";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;
    private static final long CONNECT_TIMEOUT = 10; // seconds
    private static final long READ_TIMEOUT = 10; // seconds
    private static final long WRITE_TIMEOUT = 10; // seconds
    private static Retrofit retrofit;
    private static Context appContext;
    private static volatile boolean isRefreshing = false;

    // Khởi tạo context
    public static void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        appContext = context.getApplicationContext();
        Log.d(TAG, "RetrofitClient initialized with context");
    }

    // Lấy instance Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(new RefreshTokenInterceptor())
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Log.d(TAG, "Retrofit instance created");
                }
            }
        }
        return retrofit;
    }

    // Interceptor để thêm header xác thực
    private static class AuthInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder();
            String url = originalRequest.url().toString();
            Log.d(TAG, "AuthInterceptor: Processing request for " + url);

            if (url.contains("/api/v1/auth/handle-refreshtoken") || url.contains("/api/v1/auth/logout")) {
                String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);
                if (!TextUtils.isEmpty(refreshToken)) {
                    requestBuilder.header("x-rtoken-id", refreshToken);
                    Log.d(TAG, "Added x-rtoken-id header for " + url);
                } else {
                    Log.w(TAG, "No refresh token available for " + url);
                }
            } else if (!url.contains("/api/v1/auth/")) {
                String accessToken = MyPreferences.getString(appContext, "access_token", null);
                String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);
                if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(refreshToken)) {
                    requestBuilder.header("Authorization", "Bearer " + accessToken);
                    Log.d(TAG, "Added Authorization header for " + url);
                } else {
                    Log.w(TAG, "Skipping Authorization header for " + url + ": access_token=" + accessToken + ", refresh_token=" + refreshToken);
                }
            }

            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    }

    // Interceptor để xử lý lỗi 401 và làm mới token
    private static class RefreshTokenInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            okhttp3.Response response = chain.proceed(originalRequest);
            Log.d(TAG, "RefreshTokenInterceptor: HTTP " + response.code() + " for " + originalRequest.url());

            if (response.code() == 401) {
                response.close();
                synchronized (RetrofitClient.class) {
                    if (isRefreshing) {
                        Log.d(TAG, "Another token refresh in progress, waiting for completion");
                        return waitForNewToken(chain, originalRequest);
                    }

                    String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);
                    if (TextUtils.isEmpty(refreshToken)) {
                        Log.e(TAG, "No refresh token available, initiating logout");
                        notifyLogout();
                        throw new IOException("No refresh token available");
                    }

                    isRefreshing = true;
                    Log.d(TAG, "Starting token refresh with refresh_token: " + refreshToken);
                    int retryCount = 0;

                    while (retryCount < MAX_RETRIES) {
                        Log.d(TAG, "Attempting to refresh token, attempt " + (retryCount + 1) + "/" + MAX_RETRIES);
                        try {
                            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                            Call<RefreshTokenResponse> refreshCall = apiService.refreshToken(refreshToken);
                            Response<RefreshTokenResponse> refreshResponse = refreshCall.execute();
                            Log.d(TAG, "Refresh token response: HTTP " + refreshResponse.code());

                            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                                InnerMetadata metadata = refreshResponse.body().getMetadata().getMetadata();
                                String newAccessToken = metadata.getAccessToken();
                                String newRefreshToken = metadata.getRefreshToken();

                                if (TextUtils.isEmpty(newAccessToken) || TextUtils.isEmpty(newRefreshToken)) {
                                    Log.e(TAG, "Invalid tokens in refresh response");
                                    notifyLogout();
                                    throw new IOException("Invalid tokens in refresh response");
                                }

                                MyPreferences.setString(appContext, "access_token", newAccessToken);
                                MyPreferences.setString(appContext, "refresh_token", newRefreshToken);
                                Log.d(TAG, "Successfully refreshed tokens: access_token=" + newAccessToken);

                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + newAccessToken)
                                        .build();

                                isRefreshing = false;
                                return chain.proceed(newRequest);
                            } else {
                                Log.w(TAG, "Refresh token failed: HTTP " + refreshResponse.code());
                                if (refreshResponse.code() == 401) {
                                    Log.e(TAG, "Refresh token invalid (HTTP 401), logging out");
                                    notifyLogout();
                                    throw new IOException("Invalid refresh token, HTTP 401");
                                }
                                retryCount++;
                                if (retryCount < MAX_RETRIES) {
                                    Log.d(TAG, "Retrying refresh token after delay");
                                    Thread.sleep(RETRY_DELAY_MS);
                                }
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "IOException during refresh attempt " + (retryCount + 1) + ": " + e.getMessage());
                            retryCount++;
                            if (retryCount >= MAX_RETRIES) {
                                Log.e(TAG, "Max retries reached, logging out");
                                notifyLogout();
                                throw new IOException("Failed to refresh token after " + MAX_RETRIES + " attempts: " + e.getMessage(), e);
                            }
                            try {
                                Thread.sleep(RETRY_DELAY_MS);
                            } catch (InterruptedException ie) {
                                Log.e(TAG, "Interrupted during retry delay: " + ie.getMessage());
                                Thread.currentThread().interrupt();
                                notifyLogout();
                                throw new IOException("Interrupted while retrying refresh: " + ie.getMessage(), ie);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Unexpected error during refresh attempt " + (retryCount + 1) + ": " + e.getMessage());
                            retryCount++;
                            if (retryCount >= MAX_RETRIES) {
                                Log.e(TAG, "Max retries reached, logging out");
                                notifyLogout();
                                throw new IOException("Failed to refresh token after " + MAX_RETRIES + " attempts: " + e.getMessage(), e);
                            }
                            try {
                                Thread.sleep(RETRY_DELAY_MS);
                            } catch (InterruptedException ie) {
                                Log.e(TAG, "Interrupted during retry delay: " + ie.getMessage());
                                Thread.currentThread().interrupt();
                                notifyLogout();
                                throw new IOException("Interrupted while retrying refresh: " + ie.getMessage(), ie);
                            }
                        }
                    }

                    isRefreshing = false;
                    Log.e(TAG, "Failed to refresh token after " + MAX_RETRIES + " attempts, logging out");
                    notifyLogout();
                    throw new IOException("Failed to refresh token after " + MAX_RETRIES + " attempts");
                }
            }

            return response;
        }

        private okhttp3.Response waitForNewToken(Chain chain, Request originalRequest) throws IOException {
            Log.d(TAG, "Waiting for token refresh to complete");
            synchronized (RetrofitClient.class) {
                while (isRefreshing) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted while waiting for token refresh: " + e.getMessage());
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while waiting for token refresh", e);
                    }
                }
            }

            String newAccessToken = MyPreferences.getString(appContext, "access_token", null);
            if (!TextUtils.isEmpty(newAccessToken)) {
                Log.d(TAG, "Using new access token for retry: " + newAccessToken);
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
                return chain.proceed(newRequest);
            }

            Log.e(TAG, "No valid access token after refresh, logging out");
            notifyLogout();
            throw new IOException("No valid access token available after refresh");
        }

        private void notifyLogout() {
            try {
                MyPreferences.clear(appContext);
                Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                appContext.sendBroadcast(intent);
                Log.d(TAG, "Sent logout broadcast");
            } catch (Exception e) {
                Log.e(TAG, "Failed to send logout broadcast: " + e.getMessage());
            }
        }
    }
}