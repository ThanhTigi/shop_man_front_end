package com.example.shopman.remote;

import com.example.shopman.models.ChangePasswordRequest;
import com.example.shopman.models.ForgotPasswordResponse;
import com.example.shopman.models.LoginRequest;
import com.example.shopman.models.LoginResponse;
import com.example.shopman.models.SignUpRequest;
import com.example.shopman.models.SignUpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {

    // Đảm bảo có thể sử dụng Retrofit từ bên ngoài
    private ApiService apiService;

    public ApiManager() {
            apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // Login API
    public void login(String email, String password, final ApiResponseListener<LoginResponse> listener) {
        LoginRequest request = new LoginRequest(email, password);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());

                } else {
                    listener.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void signUp(String email, String name, String password, final ApiResponseListener<SignUpResponse> listener) {
        SignUpRequest request = new SignUpRequest(email,name, password);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<SignUpResponse> call = apiService.signUp(request);

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Sign up failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void forgotPassword(String email, final ApiResponseListener<ForgotPasswordResponse> listener) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(email);

        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Forgot failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void checkOTP(String otp, final ApiResponseListener<ForgotPasswordResponse> listener) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(otp);

        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("OTP failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void changePassword(String resetToken, String newPassword, String confirmPassword,
                               final ApiResponseListener<LoginResponse> listener) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        ChangePasswordRequest request = new ChangePasswordRequest(resetToken,newPassword, confirmPassword);

        Call<LoginResponse> call = apiService.changePassword(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Change password failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

}
