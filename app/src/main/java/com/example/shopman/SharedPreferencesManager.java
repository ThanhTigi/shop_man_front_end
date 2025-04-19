package com.example.shopman;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shopman.models.User;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "ShopManPrefs";
    private static final String KEY_USER = "user";

    public static void saveUserInfo(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER, user.toJson());  // Assuming User has a toJson() method
        editor.apply();
    }

    public static User getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(KEY_USER, "");
        return User.fromJson(userJson);  // Assuming User has a fromJson() method
    }
}
