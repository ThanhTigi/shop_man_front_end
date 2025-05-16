package com.example.shopman.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.MainActivity;
import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;
import com.example.shopman.utilitis.ProductsConst;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private EditText etSearch;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private RecyclerView dealRecyclerView;
    private ProductAdapter dealAdapter;
    private List<Product> dealList;
    private TextView dealViewAll;
    private TextView dealRemainingTime;
    private RecyclerView specialOffersRecyclerView;
    private ProductAdapter specialOffersAdapter;
    private List<Product> specialOffersList;
    private RecyclerView trendingRecyclerView;
    private ProductAdapter trendingAdapter;
    private List<Product> trendingList;
    private TextView trendingViewAll;
    private TextView trendingRemainingTime;
    private ImageView bannerImageView;
    private TextView newArrivalsViewAll;

    private CountDownTimer dealCountDownTimer;
    private CountDownTimer trendingCountDownTimer;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Khởi tạo các view từ XML
        etSearch = view.findViewById(R.id.etSearch);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        dealRecyclerView = view.findViewById(R.id.dealRecyclerView);
        dealViewAll = view.findViewById(R.id.dealViewAll);
        dealRemainingTime = view.findViewById(R.id.dealRemainingTime);
        specialOffersRecyclerView = view.findViewById(R.id.specialOffersRecyclerView);
        trendingRecyclerView = view.findViewById(R.id.trendingRecyclerView);
        trendingViewAll = view.findViewById(R.id.trendingViewAll);
        trendingRemainingTime = view.findViewById(R.id.trendingRemainingTime);
        bannerImageView = view.findViewById(R.id.bannerImageView);
        newArrivalsViewAll = view.findViewById(R.id.newArrivalsViewAll);

        Log.d(TAG, "Views initialized: etSearch=" + (etSearch != null) +
                ", dealRemainingTime=" + (dealRemainingTime != null) +
                ", trendingRemainingTime=" + (trendingRemainingTime != null));

        // Kiểm tra context trước khi sử dụng
        if (getContext() == null) {
            Log.e(TAG, "Context is null, fragment not attached to activity");
            return;
        }
        Log.d(TAG, "Context is available");

        // Thanh search: Giữ logic tìm kiếm
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToSearchWithData(etSearch.getText().toString());
                return true;
            }
            return false;
        });
        Log.d(TAG, "Search listener set");

        // All Featured (Category)
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Beauty", R.drawable.ic_beauty));
        categoryList.add(new Category("Fashion", R.drawable.ic_fashion));
        categoryList.add(new Category("Kids", R.drawable.ic_kids));
        categoryList.add(new Category("Men", R.drawable.ic_mens));
        categoryList.add(new Category("Women", R.drawable.ic_womens));

        categoryAdapter = new CategoryAdapter(categoryList);
        if (getActivity() != null) {
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            categoryRecyclerView.setAdapter(categoryAdapter);
        } else {
            Log.e(TAG, "getActivity() is null for categoryRecyclerView");
        }
        Log.d(TAG, "Category RecyclerView set up");

        // Kiểm tra kích thước của ProductsConst.totalProducts
        if (ProductsConst.totalProducts == null || ProductsConst.totalProducts.size() < 9) {
            Log.e(TAG, "ProductsConst.totalProducts không đủ dữ liệu: " +
                    (ProductsConst.totalProducts != null ? ProductsConst.totalProducts.size() : "null"));
            if (getContext() != null) {
                Toast.makeText(getContext(), "Không đủ dữ liệu sản phẩm", Toast.LENGTH_LONG).show();
            }
            return; // Thoát nếu không đủ dữ liệu
        }
        Log.d(TAG, "ProductsConst.totalProducts has enough data");

        // Deal of the Day
        dealList = new ArrayList<>();
        dealList.add(ProductsConst.totalProducts.get(3));
        dealList.add(ProductsConst.totalProducts.get(4));
        dealList.add(ProductsConst.totalProducts.get(5));
        dealAdapter = new ProductAdapter(dealList);
        if (getActivity() != null) {
            dealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            dealRecyclerView.setAdapter(dealAdapter);
        }
        Log.d(TAG, "Deal RecyclerView set up");

        dealViewAll.setOnClickListener(v -> {
            try {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), DealActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi mở DealActivity: " + e.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể mở Deal of the Day", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d(TAG, "Deal ViewAll listener set");

        // Khởi tạo và chạy đếm ngược cho Deal of the Day
        if (dealRemainingTime != null) {
            startDealCountDownTimer();
        } else {
            Log.e(TAG, "dealRemainingTime is null");
        }
        Log.d(TAG, "Deal countdown setup");


        // Trending Products
        trendingList = new ArrayList<>();
        trendingList.add(ProductsConst.totalProducts.get(0));
        trendingList.add(ProductsConst.totalProducts.get(1));
        trendingList.add(ProductsConst.totalProducts.get(2));
        trendingAdapter = new ProductAdapter(trendingList);
        if (getActivity() != null) {
            trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            trendingRecyclerView.setAdapter(trendingAdapter);
        }
        Log.d(TAG, "Trending RecyclerView set up");

        trendingViewAll.setOnClickListener(v -> {
            try {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), TrendingProductActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi mở TrendingProductActivity: " + e.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể mở Trending Products", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d(TAG, "Trending ViewAll listener set");

        // Khởi tạo và chạy đếm ngược cho Trending Products
        if (trendingRemainingTime != null) {
            startTrendingCountDownTimer();
        } else {
            Log.e(TAG, "trendingRemainingTime is null");
        }
        Log.d(TAG, "Trending countdown setup");

        // New Arrivals
        bannerImageView.setImageResource(R.drawable.new_arrivals_image);
        newArrivalsViewAll.setOnClickListener(v -> {
            try {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), NewArrivalsActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi mở NewArrivalsActivity: " + e.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể mở New Arrivals", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d(TAG, "New Arrivals listener set");
    }

    private void startDealCountDownTimer() {
        Log.d(TAG, "Starting DealCountDownTimer");
        // Lấy thời gian hiện tại thực tế
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));

        // Lấy thời gian kết thúc ngày (23:59:59 hôm nay)
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        endTime.set(Calendar.MILLISECOND, 0);

        long millisUntilFinished = endTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (millisUntilFinished <= 0) millisUntilFinished = 0;

        dealCountDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                seconds %= 60;
                minutes %= 60;
                if (dealRemainingTime != null) {
                    dealRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                if (dealRemainingTime != null) {
                    dealRemainingTime.setText("00:00:00");
                }
            }
        }.start();
        Log.d(TAG, "DealCountDownTimer started");
    }


    private void startTrendingCountDownTimer() {
        Log.d(TAG, "Starting TrendingCountDownTimer");
        // Lấy thời gian hiện tại (12:59 AM +07, 16/05/2025)
        Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        endTime.set(Calendar.MILLISECOND, 0);

        long millisUntilFinished = endTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (millisUntilFinished <= 0) millisUntilFinished = 0;
            

        trendingCountDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;
                if (trendingRemainingTime != null) {
                    trendingRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    Log.e(TAG, "trendingRemainingTime is null in onTick");
                }
            }

            @Override
            public void onFinish() {
                if (trendingRemainingTime != null) {
                    trendingRemainingTime.setText("00:00:00");
                } else {
                    Log.e(TAG, "trendingRemainingTime is null in onFinish");
                }
            }
        }.start();
        Log.d(TAG, "TrendingCountDownTimer started");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dealCountDownTimer != null) {
            dealCountDownTimer.cancel();
        }
        if (trendingCountDownTimer != null) {
            trendingCountDownTimer.cancel();
        }
        Log.d(TAG, "onDestroyView called, timers canceled");
    }
}