package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView tvPrev, tvNext, tvGetStarted, tvSkip;
    private LinearLayout llDots;
    private int[] layouts = {R.layout.onboarding_screen1, R.layout.onboarding_screen2, R.layout.onboarding_screen3};
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        tvPrev = findViewById(R.id.tvPrev);
        tvNext = findViewById(R.id.tvNext);
        tvGetStarted = findViewById(R.id.tvGetStarted);
        tvSkip = findViewById(R.id.tvSkip);
        llDots = findViewById(R.id.llDots);

        // Set up ViewPager with adapter
        OnboardingAdapter adapter = new OnboardingAdapter(this, layouts);
        viewPager.setAdapter(adapter);

        // Add dot indicators
        addDots(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == 0) {
                    tvPrev.setVisibility(View.GONE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                } else if (position == layouts.length - 1) {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.GONE);
                    tvGetStarted.setVisibility(View.VISIBLE);
                } else {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                }
            }
        });

        // Navigation button listeners
        tvPrev.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        tvNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
        tvGetStarted.setOnClickListener(v -> {
            // Navigate to the login screen
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        tvSkip.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDots(int currentPage) {
        llDots.removeAllViews();
        dots = new TextView[layouts.length];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(18);
            dots[i].setTextColor(currentPage == i ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.darker_gray));
            llDots.addView(dots[i]);
        }
    }
}

// Create an adapter for ViewPager
class OnboardingAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    private final int[] layouts;

    public OnboardingAdapter(OnboardingActivity activity, int[] layouts) {
        super(activity);
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public androidx.fragment.app.Fragment createFragment(int position) {
        return new OnboardingFragment(layouts[position]);
    }

    @Override
    public int getItemCount() {
        return layouts.length;
    }
}

// Create a fragment for each page
class OnboardingFragment extends androidx.fragment.app.Fragment {
    private int layoutRes;

    public OnboardingFragment(int layoutRes) {
        super(R.layout.fragment_onboarding);
        this.layoutRes = layoutRes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutRes, container, false);
        return view;
    }
}