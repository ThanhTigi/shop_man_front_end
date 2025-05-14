package com.example.shopman.remote;

import android.content.Context;
import android.text.TextUtils;

import com.example.shopman.models.auth.RefreshTokenResponse;
import com.example.shopman.utilitis.MyPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://shopman.onrender.com";
    private static Context appContext;
    private static boolean isRefreshing = false;

    // Khởi tạo context
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .addInterceptor(new RefreshTokenInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Interceptor để thêm header
    private static class AuthInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder requestBuilder = originalRequest.newBuilder();

            String url = originalRequest.url().toString();
            if (url.contains("/api/v1/auth/refresh-token") || url.contains("/api/v1/auth/logout")) {
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

            if (response.code() == 401) {
                synchronized (RetrofitClient.class) {
                    if (isRefreshing) {
                        // Nếu đang làm mới token, chờ và thử lại với token mới
                        return waitForNewToken(chain, originalRequest);
                    }

                    isRefreshing = true;
                    String refreshToken = MyPreferences.getString(appContext, "refresh_token", null);

                    if (!TextUtils.isEmpty(refreshToken)) {
                        try {
                            // Gọi API refresh token
                            ApiService apiService = getClient().create(ApiService.class);
                            Call<RefreshTokenResponse> refreshCall = apiService.refreshToken(refreshToken);
                            Response<RefreshTokenResponse> refreshResponse = refreshCall.execute();

                            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                                String newAccessToken = refreshResponse.body().getMetadata().getMetadata().getAccessToken();
                                String newRefreshToken = refreshResponse.body().getMetadata().getMetadata().getRefreshToken();

                                // Lưu token mới
                                MyPreferences.setString(appContext, "access_token", newAccessToken);
                                MyPreferences.setString(appContext, "refresh_token", newRefreshToken);

                                // Thử lại yêu cầu với token mới
                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + newAccessToken)
                                        .build();
                                return chain.proceed(newRequest);
                            } else {
                                // Refresh token thất bại
                                MyPreferences.clear(appContext);
                                throw new IOException("Refresh token failed");
                            }
                        } catch (Exception e) {
                            MyPreferences.clear(appContext);
                            throw new IOException("Error refreshing token", e);
                        } finally {
                            isRefreshing = false;
                        }
                    } else {
                        isRefreshing = false;
                        MyPreferences.clear(appContext);
                        throw new IOException("No refresh token available");
                    }
                }
            }
            return response;
        }

        private okhttp3.Response waitForNewToken(Chain chain, Request originalRequest) throws IOException {
            synchronized (RetrofitClient.class) {
                while (isRefreshing) {
                    try {
                        Thread.sleep(100); // Chờ 100ms trước khi kiểm tra lại
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
    }
}