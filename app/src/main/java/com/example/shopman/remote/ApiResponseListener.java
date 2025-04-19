package com.example.shopman.remote;

public interface ApiResponseListener<T> {
    void onSuccess(T response);
    void onError(String errorMessage);
}
