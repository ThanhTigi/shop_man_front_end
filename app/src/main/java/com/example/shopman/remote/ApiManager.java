package com.example.shopman.remote;

import android.content.Context;
import android.util.Log;

import com.example.shopman.models.FcmTokenRequest;
import com.example.shopman.models.changepassword.request.ChangePasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.request.ForgotPasswordResponse;
import com.example.shopman.models.changepassword.response.ChangePasswordResponse;
import com.example.shopman.models.checkotp.CheckOTPResponse;
import com.example.shopman.models.login.GoogleLoginRequest;
import com.example.shopman.models.login.LoginRequest;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.utilitis.MyPreferences;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {
    private ApiService apiService;
    private Context context;

    public ApiManager(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    public void login(String email, String password, final ApiResponseListener<LoginResponse> listener) {
        Log.d("ApiManager", "Login Request: email=" + email);
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("ApiManager", "Login Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    MyPreferences.setString(context, "access_token", loginResponse.getMetadata().getMetadata().getTokens().getAccessToken());
                    MyPreferences.setString(context, "refresh_token", loginResponse.getMetadata().getMetadata().getTokens().getRefreshToken());
                    MyPreferences.setString(context, "current_user_meta_data", new Gson().toJson(loginResponse.getMetadata().getMetadata().getUser()));
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("ApiManager", "Login Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void loginWithGoogle(String idToken, final ApiResponseListener<LoginResponse> listener) {
        Log.d("ApiManager", "Google Login Request: idToken=" + idToken);
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<LoginResponse> call = apiService.loginWithGoogle(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("ApiManager", "Google Login Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    MyPreferences.setString(context, "access_token", loginResponse.getMetadata().getMetadata().getTokens().getAccessToken());
                    MyPreferences.setString(context, "refresh_token", loginResponse.getMetadata().getMetadata().getTokens().getRefreshToken());
                    MyPreferences.setString(context, "current_user_meta_data", new Gson().toJson(loginResponse.getMetadata().getMetadata().getUser()));
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Google login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("ApiManager", "Google Login Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void signUp(SignUpRequest request, ApiResponseListener<SignUpResponse> listener) {
        Log.d("ApiManager", "Sign Up Request: " + new Gson().toJson(request));
        Call<SignUpResponse> call = apiService.signUp(request);
        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Log.d("ApiManager", "Sign Up Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    SignUpResponse signUpResponse = response.body();
                    MyPreferences.setString(context, "access_token", signUpResponse.getMetaData().getMetadata().getTokens().getAccessToken());
                    MyPreferences.setString(context, "refresh_token", signUpResponse.getMetaData().getMetadata().getTokens().getRefreshToken());
                    MyPreferences.setString(context, "current_user_meta_data", new Gson().toJson(signUpResponse.getMetaData().getMetadata().getUser()));
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Sign up failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e("ApiManager", "Sign Up Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void changePassword(ChangePasswordRequest request, ApiResponseListener<ChangePasswordResponse> listener) {
        Log.d("ApiManager", "Change Password Request: " + new Gson().toJson(request));
        Call<ChangePasswordResponse> call = apiService.changePassword(request);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                Log.d("ApiManager", "Change Password Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    ChangePasswordResponse changePasswordResponse = response.body();
                    MyPreferences.setString(context, "access_token", changePasswordResponse.getMetaData().getMetadata().getTokens().getAccessToken());
                    MyPreferences.setString(context, "refresh_token", changePasswordResponse.getMetaData().getMetadata().getTokens().getRefreshToken());
                    MyPreferences.setString(context, "current_user_meta_data", new Gson().toJson(changePasswordResponse.getMetaData().getMetadata().getUser()));
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to change password: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.e("ApiManager", "Change Password Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void forgotPassword(ForgotPasswordRequest request, ApiResponseListener<ForgotPasswordResponse> listener) {
        Log.d("ApiManager", "Forgot Password Request: " + new Gson().toJson(request));
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(request);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                Log.d("ApiManager", "Forgot Password Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to send OTP: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Log.e("ApiManager", "Forgot Password Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void checkOTP(OTPRequest request, ApiResponseListener<CheckOTPResponse> listener) {
        Log.d("ApiManager", "Check OTP Request: " + new Gson().toJson(request));
        Call<CheckOTPResponse> call = apiService.checkOTP(request);
        call.enqueue(new Callback<CheckOTPResponse>() {
            @Override
            public void onResponse(Call<CheckOTPResponse> call, Response<CheckOTPResponse> response) {
                Log.d("ApiManager", "Check OTP Response: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to verify OTP: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CheckOTPResponse> call, Throwable t) {
                Log.e("ApiManager", "Check OTP Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public void getUserProfile(String accessToken, ApiResponseListener<GetUserProfileResponse> listener) {
        Call<GetUserProfileResponse> call = apiService.getUserProfile("Bearer " + accessToken);
        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to fetch profile");
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateUserProfile(String accessToken, UpdateProfileRequest request, ApiResponseListener<GetUserProfileResponse> listener) {
        Call<GetUserProfileResponse> call = apiService.updateUserProfile("Bearer " + accessToken, request);
        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onError("Failed to update profile");
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void updateFcmToken(String accessToken, String fcmToken, ApiResponseListener<Void> listener) {
        Log.d("ApiManager", "Update FCM Token Request: fcmToken=" + fcmToken);
        FcmTokenRequest request = new FcmTokenRequest(fcmToken);
        Call<Void> call = apiService.updateFcmToken("Bearer " + accessToken, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("ApiManager", "Update FCM Token Response: " + response.code());
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else {
                    listener.onError("Failed to update FCM token: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ApiManager", "Update FCM Token Error: " + t.getMessage());
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }

    public interface BooleanCallback {
        void onResult(boolean result);
    }
}