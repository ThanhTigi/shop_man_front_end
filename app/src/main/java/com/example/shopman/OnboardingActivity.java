package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView tvPrev, tvNext, tvGetStarted, tvSkip;
    private LinearLayout llDots;
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
        OnboardingAdapter adapter = new OnboardingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Add dot indicators
        addDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == 0) {
                    tvPrev.setVisibility(View.GONE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                } else if (position == adapter.getCount() - 1) {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.GONE);
                    tvGetStarted.setVisibility(View.VISIBLE);
                } else {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Navigation button listeners
        tvPrev.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        tvNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
        tvGetStarted.setOnClickListener(v -> {
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
        dots = new TextView[3];  // Số lượng màn hình onboarding
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(18);
            dots[i].setTextColor(currentPage == i ? getResources().getColor(android.R.color.black) : getResources().getColor(android.R.color.darker_gray));
            llDots.addView(dots[i]);
        }
    }
}