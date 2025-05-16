package com.example.shopman;

import android.app.Application;
import android.util.Log;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class ShopManApplication extends Application {
    private static final String TAG = "ShopManApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        initCloudinary();
    }

    private void initCloudinary() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dirb3hhg1"); // Xác nhận Cloud Name từ Cloudinary Dashboard
            MediaManager.init(this, config);
            Log.d(TAG, "Cloudinary initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Cloudinary: " + e.getMessage());
        }
    }
}