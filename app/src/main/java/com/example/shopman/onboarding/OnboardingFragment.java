package com.example.shopman.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class OnboardingFragment extends Fragment {

    private static final String ARG_LAYOUT = "layout";

    public static OnboardingFragment newInstance(int layoutRes) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutRes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutRes = getArguments().getInt(ARG_LAYOUT);
        return inflater.inflate(layoutRes, container, false);
    }
}