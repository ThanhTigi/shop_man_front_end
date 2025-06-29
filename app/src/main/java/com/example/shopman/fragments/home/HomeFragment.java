package com.example.shopman.fragments.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.activities.CampaignDetailsActivity;
import com.example.shopman.activities.CategoryProductsActivity;
import com.example.shopman.activities.DealActivity;
import com.example.shopman.activities.LoginActivity;
import com.example.shopman.activities.ShopDetailActivity;
import com.example.shopman.adapters.BannerAdapter;
import com.example.shopman.adapters.CategoryAdapter;
import com.example.shopman.adapters.DealProductAdapter;
import com.example.shopman.models.Banner.Banner;
import com.example.shopman.models.Banner.BannerResponse;
import com.example.shopman.models.Category;
import com.example.shopman.models.category.CategoryResponse;
import com.example.shopman.models.DealofTheDay.DealProductResponse;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.SpacesItemDecoration;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int PAGE_SIZE = 10; // Chỉ lấy 10 sản phẩm

    private EditText etSearch;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ProgressBar categoryProgressBar;
    private RecyclerView dealRecyclerView;
    private DealProductAdapter dealAdapter;
    private List<SearchProduct> dealList; // Sử dụng Product thay vì DealProduct
    private MaterialButton btnViewAllDeal;
    private TextView dealRemainingTime;
    private ProgressBar dealProgressBar;
    private ViewPager2 bannerViewPager;
    private ProgressBar bannerProgressBar;
    private CircleIndicator3 bannerIndicator;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private CountDownTimer bannerAutoSlideTimer;
    private CountDownTimer dealCountDownTimer;
    private ApiManager apiManager;
    private BroadcastReceiver logoutReceiver;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Khởi tạo views
        etSearch = view.findViewById(R.id.etSearch);
        ImageView ivSearch = view.findViewById(R.id.ivSearch);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryProgressBar = view.findViewById(R.id.categoryProgressBar);
        dealRecyclerView = view.findViewById(R.id.dealRecyclerView);
        btnViewAllDeal = view.findViewById(R.id.btnViewAllDeal);
        dealRemainingTime = view.findViewById(R.id.dealRemainingTime);
        dealProgressBar = view.findViewById(R.id.dealProgressBar);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerProgressBar = view.findViewById(R.id.bannerProgressBar);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);

        if (getContext() == null) {
            Log.e(TAG, "Context is null, fragment not attached to activity");
            Toast.makeText(requireContext(), "Lỗi: Fragment không gắn với Activity", Toast.LENGTH_SHORT).show();
            return;
        }

        apiManager = new ApiManager(getContext());

        // Đăng ký BroadcastReceiver cho lỗi 401
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                startActivity(loginIntent);
                if (getActivity() != null) getActivity().finish();
            }
        };
        requireContext().registerReceiver(logoutReceiver, new IntentFilter("com.example.shopman.ACTION_LOGOUT"));

        // Search listener
        ivSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                String query = etSearch.getText().toString().trim();
                ((MainActivity) getActivity()).switchToSearchWithData(query);
            } else {
                Toast.makeText(getContext(), "Không thể thực hiện tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        // Khởi tạo danh mục
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, slug -> {
            String categoryName = categoryList.stream()
                    .filter(c -> c.getSlug().equals(slug))
                    .findFirst()
                    .map(Category::getName)
                    .orElse("Category");
            String categoryImageUrl = categoryList.stream()
                    .filter(c -> c.getSlug().equals(slug))
                    .findFirst()
                    .map(Category::getThumbUrl)
                    .orElse(null);
            Intent intent = CategoryProductsActivity.createIntent(getContext(), slug, categoryName, categoryImageUrl);
            startActivity(intent);
        });
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.addItemDecoration(new SpacesItemDecoration(-10));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Khởi tạo banner
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, this::handleBannerClick);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setOffscreenPageLimit(3);
        bannerIndicator.setViewPager(bannerViewPager);

        // Khởi tạo Deal of the Day
        dealList = new ArrayList<>();
        dealAdapter = new DealProductAdapter(getContext(), dealList);
        dealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        dealRecyclerView.addItemDecoration(new SpacesItemDecoration(-10));
        dealRecyclerView.setAdapter(dealAdapter);
        btnViewAllDeal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DealActivity.class);
            startActivity(intent);
        });
        startDealCountDownTimer();

        // Tải dữ liệu
        loadBanners();
        loadCategories();
        loadDeals();
    }

    private void loadDeals() {
        Log.d(TAG, "Starting loadDeals");
        dealProgressBar.setVisibility(View.VISIBLE);
        apiManager.getDealOfTheDay("", PAGE_SIZE, null, null, null, null, new ApiResponseListener<DealProductResponse>() {
            @Override
            public void onSuccess(DealProductResponse response) {
                dealProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "Deal response received: " + new Gson().toJson(response));
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    List<SearchProduct> products = response.getMetadata().getMetadata().getData();
                    dealList.clear();
                    if (products != null && !products.isEmpty()) {
                        dealList.addAll(products.subList(0, Math.min(products.size(), PAGE_SIZE))); // Lấy tối đa 10 sản phẩm
                        dealAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Loaded " + dealList.size() + " deal products");
                    } else {
                        Log.w(TAG, "No deal products available, products list is null or empty");
                        Toast.makeText(getContext(), "Không có sản phẩm deal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.w(TAG, "Invalid deal response, metadata or inner metadata is null");
                    Toast.makeText(getContext(), "Không thể tải deal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                dealProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load deals: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải deal: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadBanners() {
        Log.d(TAG, "Starting loadBanners");
        bannerProgressBar.setVisibility(View.VISIBLE);
        apiManager.getBanners(new ApiResponseListener<BannerResponse>() {
            @Override
            public void onSuccess(BannerResponse response) {
                bannerProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "Banner response received: " + new Gson().toJson(response));
                if (response != null && response.getMetadata() != null) {
                    bannerList.clear();
                    for (Banner banner : response.getMetadata().getBanners()) {
                        if ("active".equals(banner.getStatus())) {
                            bannerList.add(banner);
                        }
                    }
                    bannerAdapter.notifyDataSetChanged();
                    bannerIndicator.createIndicators(bannerList.size(), 0);
                    bannerViewPager.setVisibility(bannerList.isEmpty() ? View.GONE : View.VISIBLE);
                    if (!bannerList.isEmpty()) startBannerAutoSlide();
                } else {
                    Log.w(TAG, "No banners available");
                    Toast.makeText(getContext(), "Không thể tải banner", Toast.LENGTH_SHORT).show();
                    bannerViewPager.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                bannerProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load banners: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải banner: " + errorMessage, Toast.LENGTH_SHORT).show();
                bannerViewPager.setVisibility(View.GONE);
            }
        });
    }

    private void handleBannerClick(Banner banner) {
        String linkType = banner.getLinkType();
        String slug = banner.getSlug();
        if ("campaign".equals(linkType)) {
            Intent intent = new Intent(getContext(), CampaignDetailsActivity.class);
            intent.putExtra(CampaignDetailsActivity.EXTRA_CAMPAIGN_SLUG, slug);
            startActivity(intent);
        } else if ("shop".equals(linkType)) {
            Intent intent = new Intent(getContext(), ShopDetailActivity.class);
            intent.putExtra(ShopDetailActivity.EXTRA_SHOP_SLUG, slug);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Loại liên kết không hỗ trợ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCategories() {
        categoryProgressBar.setVisibility(View.VISIBLE);
        apiManager.getCategories(new ApiResponseListener<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse response) {
                categoryProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.getMetadata().getMetadata());
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "No categories available");
                    Toast.makeText(getContext(), "Không có danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                categoryProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "Failed to load categories: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải danh mục: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startBannerAutoSlide() {
        if (bannerAutoSlideTimer != null) bannerAutoSlideTimer.cancel();
        bannerAutoSlideTimer = new CountDownTimer(3600000, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (bannerList.size() > 0 && isAdded()) {
                    int currentItem = bannerViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerList.size();
                    bannerViewPager.setCurrentItem(nextItem, true);
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) startBannerAutoSlide();
            }
        }.start();
    }

    private void startDealCountDownTimer() {
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        long millisUntilFinished = endTime.getTimeInMillis() - System.currentTimeMillis();
        if (millisUntilFinished <= 0) millisUntilFinished = 0;

        dealCountDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    seconds %= 60;
                    minutes %= 60;
                    dealRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) dealRemainingTime.setText("00:00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dealCountDownTimer != null) dealCountDownTimer.cancel();
        if (bannerAutoSlideTimer != null) bannerAutoSlideTimer.cancel();
        if (logoutReceiver != null) requireContext().unregisterReceiver(logoutReceiver);
        Log.d(TAG, "onDestroyView called");
    }
}