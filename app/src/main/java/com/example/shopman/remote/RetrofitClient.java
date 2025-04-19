package com.example.shopman.remote;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://4a5b-113-190-225-135.ngrok-free.app/"; // sửa lại URL của bạn

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}