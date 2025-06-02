package com.example.shopman.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.shopman.utilitis.MyPreferences;
import com.example.shopman.R;
import com.example.shopman.activities.LoginActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView numberOfPageTxt;
    private TextView tvHidePrev, tvPrev, tvNext, tvGetStarted, tvSkip;
    private LinearLayout llDots;
    private TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyPreferences.getBoolean(this,"new_user", false))
        {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        numberOfPageTxt = findViewById(R.id.numberOfPageTxt);
        tvHidePrev = findViewById(R.id.tvHidePrev);
        tvPrev = findViewById(R.id.tvPrev);
        tvNext = findViewById(R.id.tvNext);
        tvGetStarted = findViewById(R.id.tvGetStarted);
        tvSkip = findViewById(R.id.tvSkip);
        llDots = findViewById(R.id.llDots);

        OnboardingAdapter adapter = new OnboardingAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        addDots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                addDots(position);
                if (position == 0) {
                    tvPrev.setVisibility(View.GONE);
                    tvHidePrev.setVisibility(View.VISIBLE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                } else if (position == adapter.getCount() - 1) {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvHidePrev.setVisibility(View.GONE);
                    tvNext.setVisibility(View.GONE);
                    tvGetStarted.setVisibility(View.VISIBLE);
                } else {
                    tvPrev.setVisibility(View.VISIBLE);
                    tvHidePrev.setVisibility(View.GONE);
                    tvNext.setVisibility(View.VISIBLE);
                    tvGetStarted.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tvPrev.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));
        tvNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
        tvGetStarted.setOnClickListener(v -> {
            MyPreferences.setBoolean(this,"new_user",true);
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        tvSkip.setOnClickListener(v -> {
            MyPreferences.setBoolean(this,"new_user",true);
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDots(int currentPage) {
        numberOfPageTxt.setText((currentPage + 1) + "/3");
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