package com.example.shopman.utilitis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;

public class MyPreferences {
    private static final String TAG = "MyPreferences";
    private static final String PREF_NAME = "SecurePrefs";
    private static SharedPreferences prefs;

    // Khởi tạo SharedPreferences, ưu tiên EncryptedSharedPreferences
    private static SharedPreferences getPrefs(Context context) {
        if (prefs == null) {
            try {
                String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
                prefs = EncryptedSharedPreferences.create(
                        PREF_NAME,
                        masterKeyAlias,
                        context.getApplicationContext(),
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize EncryptedSharedPreferences: " + e.getMessage());
                // Fallback to regular SharedPreferences if encryption fails
                prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            }
        }
        return prefs;
    }

    // Kiểm tra context
    private static void checkContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
    }

    // Save
    public static void setInt(Context context, String key, int value) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to set int for key " + key + ": " + e.getMessage());
        }
    }

    public static void setString(Context context, String key, String value) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putString(key, value);
            editor.apply();
            Log.d(TAG, "Saved string for key " + key);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set string for key " + key + ": " + e.getMessage());
        }
    }

    public static void setBoolean(Context context, String key, boolean value) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to set boolean for key " + key + ": " + e.getMessage());
        }
    }

    public static void setFloat(Context context, String key, float value) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putFloat(key, value);
            editor.apply();
        } catch (Exception e) {
            Log.e(TAG, "Failed to set float for key " + key + ": " + e.getMessage());
        }
    }

    // Get
    public static int getInt(Context context, String key, int defaultValue) {
        checkContext(context);
        try {
            return getPrefs(context).getInt(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get int for key " + key + ": " + e.getMessage());
            return defaultValue;
        }
    }

    public static String getString(Context context, String key, String defaultValue) {
        checkContext(context);
        try {
            return getPrefs(context).getString(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get string for key " + key + ": " + e.getMessage());
            return defaultValue;
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        checkContext(context);
        try {
            return getPrefs(context).getBoolean(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get boolean for key " + key + ": " + e.getMessage());
            return defaultValue;
        }
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        checkContext(context);
        try {
            return getPrefs(context).getFloat(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get float for key " + key + ": " + e.getMessage());
            return defaultValue;
        }
    }

    // Remove and clear
    public static void remove(Context context, String key) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.remove(key);
            editor.apply();
            Log.d(TAG, "Removed key " + key);
        } catch (Exception e) {
            Log.e(TAG, "Failed to remove key " + key + ": " + e.getMessage());
        }
    }

    public static void clear(Context context) {
        checkContext(context);
        try {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.clear();
            editor.apply();
            Log.d(TAG, "Cleared all preferences");
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear preferences: " + e.getMessage());
        }
    }
}