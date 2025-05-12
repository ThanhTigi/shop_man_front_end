package com.example.shopman.remote;

import com.example.shopman.models.changepassword.ChangePasswordRequest;
import com.example.shopman.models.changepassword.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.ForgotPasswordResponse;
import com.example.shopman.models.login.LoginRequest;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileResponse;
import com.example.shopman.models.searchproducts.SearchProductsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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

    @GET("/api/v1/user/profile")
    Call<GetUserProfileResponse> getUserProfile(@Header("authorization") String token);

    @PUT("/api/v1/user/profile/update")
    Call<UpdateProfileResponse> updateUserProfile(@Header("authorization") String token,@Body UpdateProfileRequest request);

    @GET("/api/v1/product/search")
    Call<SearchProductsResponse> searchProducts(@Query("query") String query);
}
