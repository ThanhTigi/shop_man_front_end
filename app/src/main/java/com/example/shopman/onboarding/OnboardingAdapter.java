package com.example.shopman.onboarding;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.shopman.R;

public class OnboardingAdapter extends FragmentPagerAdapter {
    private final int[] layouts = {R.layout.onboarding_screen1, R.layout.onboarding_screen2, R.layout.onboarding_screen3};

    public OnboardingAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        return OnboardingFragment.newInstance(layouts[position]);
    }

    @Override
    public int getCount() {
        return layouts.length;
    }
}