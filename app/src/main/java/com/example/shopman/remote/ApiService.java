package com.example.shopman.remote;

import com.example.shopman.models.ChangePasswordRequest;
import com.example.shopman.models.ForgotPasswordRequest;
import com.example.shopman.models.ForgotPasswordResponse;
import com.example.shopman.models.LoginRequest;
import com.example.shopman.models.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.SignUpRequest;
import com.example.shopman.models.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/v1/auth/signup")
    Call<SignUpResponse> signUp(@Body SignUpRequest request);

    @POST("/api/v1/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/api/v1/auth/check-otp")
    Call<ForgotPasswordResponse> checkOTP(@Body OTPRequest request);

    @POST("/api/v1/auth/change-password")
    Call<LoginResponse> changePassword(@Body ChangePasswordRequest request);
}
