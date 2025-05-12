package com.example.shopman.remote;

import android.content.Context;

import com.example.shopman.utilitis.MyPreferences;
import com.example.shopman.models.changepassword.ChangePasswordRequest;
import com.example.shopman.models.changepassword.ForgotPasswordRequest;
import com.example.shopman.models.changepassword.ForgotPasswordResponse;
import com.example.shopman.models.login.LoginRequest;
import com.example.shopman.models.login.LoginResponse;
import com.example.shopman.models.OTPRequest;
import com.example.shopman.models.signup.SignUpRequest;
import com.example.shopman.models.signup.SignUpResponse;
import com.example.shopman.models.login.UserMetadata;
import com.example.shopman.models.profile.getuserprofile.GetUserProfileResponse;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileRequest;
import com.example.shopman.models.profile.updateuserprofile.UpdateProfileResponse;
import com.example.shopman.models.searchproducts.SearchProductsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {

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
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ForgotPasswordResponse> call = apiService.forgotPassword(request);

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
        OTPRequest request = new OTPRequest(otp);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ForgotPasswordResponse> call = apiService.checkOTP(request);

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

    public void checkUserLogin(Context context, BooleanCallback callback)
    {
        if (MyPreferences.getString(context,"current_user_meta_data",null) == null)
        {
            callback.onResult(false);
        }
        String userJson = MyPreferences.getString(context,"current_user_meta_data","");
        UserMetadata userMetadata = UserMetadata.fromJson(MyPreferences.getString(context,"current_user_meta_data",""));
        if (userMetadata == null || userMetadata.getTokens() == null) {
            callback.onResult(false);
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<GetUserProfileResponse> call = apiService.getUserProfile("Bearer " + userMetadata.getTokens().getAccessToken());

        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }

    public void getUserProfile(String accessToken, final ApiResponseListener<GetUserProfileResponse> listener)
    {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<GetUserProfileResponse> call = apiService.getUserProfile("Bearer " + accessToken);

        call.enqueue(new Callback<GetUserProfileResponse>() {
            @Override
            public void onResponse(Call<GetUserProfileResponse> call, Response<GetUserProfileResponse> response) {
                if (response.isSuccessful())
                {
                    listener.onSuccess(response.body());
                }
                else
                {
                    listener.onError(response.message());
                }

            }

            @Override
            public void onFailure(Call<GetUserProfileResponse> call, Throwable t) {
                listener.onError("Network Error: " + t.getMessage());
            }
        });
    }


    public void updateUserProfile(String token, UpdateProfileRequest request, BooleanCallback callback)
    {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<UpdateProfileResponse> call = apiService.updateUserProfile("Bearer " + token, request);

        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                System.out.println("==" + response.body());
                if (response.isSuccessful()) {
                    callback.onResult(true);
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                callback.onResult(false);
            }
        });

    }


    public interface BooleanCallback {
        void onResult(boolean result);
    }

}

