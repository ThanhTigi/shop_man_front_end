package com.example.shopman.remote;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.shopman.models.auth.InnerMetadata;
import com.example.shopman.models.auth.RefreshTokenResponse;
import com.example.shopman.utilitis.MyPreferences;

import java.io.IOException;

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
    private static final String BASE_URL = "https://shopman.onrender.com/"; // Thay bằng URL của bạn
    private static final int MAX_RETRIES = 3;
    private static Retrofit retrofit;
    private static Context appContext;
    private static volatile boolean isRefreshing = false;

    // Khởi tạo context
    public static void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        appContext = context.getApplicationContext();
    }

    // Lấy instance Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Log.d(TAG, message));
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .addInterceptor(new AuthInterceptor())
                            .addInterceptor(new RefreshTokenInterceptor())
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
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
            try {
                if (url.contains("/api/v1/auth/handle-refreshtoken") || url.contains("/api/v1/auth/logout")) {
                    String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);
                    if (!TextUtils.isEmpty(refreshToken)) {
                        requestBuilder.header("x-rtoken-id", refreshToken);
                    }
                } else if (!url.contains("/api/v1/auth/")) {
                    String accessToken = MyPreferences.getString(appContext, "access_token", null);
                    if (!TextUtils.isEmpty(accessToken)) {
                        requestBuilder.header("Authorization", "Bearer " + accessToken);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in AuthInterceptor: " + e.getMessage());
            }

            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
    }

    // Interceptor để xử lý lỗi 401 và làm mới token
    private static class RefreshTokenInterceptor implements Interceptor {
        private static final String TAG = "RetrofitClient";
        private static final int MAX_RETRIES = 3;
        private static volatile boolean isRefreshing = false;

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            okhttp3.Response response = chain.proceed(originalRequest);
            Log.d(TAG, "intercept: HTTP " + response.code() + " for " + originalRequest.url());

            if (response.code() == 401) {
                response.close(); // Đóng response cũ trước khi gọi request mới

                synchronized (RetrofitClient.class) {
                    if (isRefreshing) {
                        // Nếu đang refresh thì chờ token mới
                        return waitForNewToken(chain, originalRequest);
                    }

                    isRefreshing = true;
                    int retryCount = 0;

                    while (retryCount < MAX_RETRIES) {
                        try {
                            String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);
                            if (TextUtils.isEmpty(refreshToken)) {
                                throw new IOException("No refresh token available");
                            }

                            // Gọi API để làm mới token
                            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                            Call<RefreshTokenResponse> refreshCall = apiService.refreshToken(refreshToken);
                            Response<RefreshTokenResponse> refreshResponse = refreshCall.execute();

                            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                                String newAccessToken = refreshResponse.body().getMetadata().getMetadata().getAccessToken();
                                String newRefreshToken = refreshResponse.body().getMetadata().getMetadata().getRefreshToken();

                                // Lưu token mới
                                MyPreferences.setString(appContext, "access_token", newAccessToken);
                                MyPreferences.setString(appContext, "refresh_token", newRefreshToken);
                                Log.d(TAG, "Successfully refreshed tokens");

                                // Tạo request mới với access token mới
                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + newAccessToken)
                                        .build();

                                isRefreshing = false;
                                return chain.proceed(newRequest);
                            } else {
                                retryCount++;
                                Thread.sleep(500);
                                Log.w(TAG, "Refresh token attempt " + retryCount + " failed: HTTP " + refreshResponse.code());
                            }

                        } catch (Exception e) {
                            retryCount++;
                            Log.e(TAG, "Error refreshing token: " + e.getMessage());
                        }
                    }

                    // Nếu retry MAX lần không thành công, xóa token và logout
                    isRefreshing = false;
                    MyPreferences.clear(appContext);
                    notifyLogout();
                    Log.d(TAG, "Cleared preferences due to failed token refresh");
                    throw new IOException("Failed to refresh token after " + MAX_RETRIES + " attempts");
                }
            }

            return response;
        }

        private okhttp3.Response waitForNewToken(Chain chain, Request originalRequest) throws IOException {
            synchronized (RetrofitClient.class) {
                while (isRefreshing) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new IOException("Interrupted while waiting for token refresh", e);
                    }
                }
            }
            String newAccessToken = MyPreferences.getString(appContext, "access_token", null);
            if (!TextUtils.isEmpty(newAccessToken)) {
                Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
                return chain.proceed(newRequest);
            }
            throw new IOException("No valid access token available");
        }

        private void notifyLogout() {
            try {
                Intent intent = new Intent("com.example.shopman.ACTION_LOGOUT");
                appContext.sendBroadcast(intent);
                Log.d(TAG, "Sent logout broadcast");
            } catch (Exception e) {
                Log.e(TAG, "Failed to send logout broadcast: " + e.getMessage());
            }
        }
    }

}