package com.example.shopman;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.shopman.auth.LoginActivity;

public class SettingFragment extends Fragment {
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        // Tìm LinearLayout của nút Log out
        LinearLayout logOutButton = view.findViewById(R.id.logOutButton);

        // Thiết lập sự kiện click
        logOutButton.setOnClickListener(v -> {
            MyPreferences.setString(getActivity(), "current_user_meta_data", "");

            // Chuyển về giao diện login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack hoạt động
            startActivity(intent);
            getActivity().finish(); // Kết thúc activity hiện tại
        });

        return view;
    }

    private void clearUserData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
