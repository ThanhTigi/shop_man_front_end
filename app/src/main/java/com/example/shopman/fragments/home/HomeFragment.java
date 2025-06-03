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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopman.MainActivity;
import com.example.shopman.R;
import com.example.shopman.ShopActivity;
import com.example.shopman.activities.CampaignActivity;
import com.example.shopman.activities.CategoryProductsActivity;
import com.example.shopman.activities.DealActivity;
import com.example.shopman.activities.NewArrivalsActivity;
import com.example.shopman.adapters.BannerAdapter;
import com.example.shopman.adapters.CategoryAdapter;
import com.example.shopman.adapters.ProductAdapter;
import com.example.shopman.models.Banner.Banner;
import com.example.shopman.models.Banner.BannerResponse;
import com.example.shopman.models.Category;
import com.example.shopman.models.category.CategoryResponse;
import com.example.shopman.models.wishlist.WishlistProductDetail;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.SpacesItemDecoration;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int PAGE_SIZE = 10;

    private EditText etSearch;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ProgressBar categoryProgressBar;
    private RecyclerView dealRecyclerView;
    private ProductAdapter dealAdapter;
    private List<WishlistProductDetail> dealList;
    private TextView dealViewAll;
    private TextView dealRemainingTime;
    private RecyclerView specialOffersRecyclerView;
    private ProductAdapter specialOffersAdapter;
    private List<WishlistProductDetail> specialOffersList;
    private RecyclerView trendingRecyclerView;
    private ProductAdapter trendingAdapter;
    private List<WishlistProductDetail> trendingList;
    private TextView trendingViewAll;
    private TextView trendingRemainingTime;
    private ImageView bannerImageView;
    private TextView newArrivalsViewAll;
    private ViewPager2 bannerViewPager;
    private ProgressBar bannerProgressBar;
    private CircleIndicator3 bannerIndicator;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private CountDownTimer bannerAutoSlideTimer;
    private CountDownTimer dealCountDownTimer;
    private CountDownTimer trendingCountDownTimer;
    private ApiManager apiManager;

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

        // Khởi tạo views
        Log.d(TAG, "Initializing views");
        etSearch = view.findViewById(R.id.etSearch);
        ImageView ivSearch = view.findViewById(R.id.ivSearch);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryProgressBar = view.findViewById(R.id.categoryProgressBar);
        dealRecyclerView = view.findViewById(R.id.dealRecyclerView);
        dealViewAll = view.findViewById(R.id.dealViewAll);
        dealRemainingTime = view.findViewById(R.id.dealRemainingTime);
        specialOffersRecyclerView = view.findViewById(R.id.specialOffersRecyclerView);
        trendingRecyclerView = view.findViewById(R.id.trendingRecyclerView);
        trendingViewAll = view.findViewById(R.id.trendingViewAll);
        trendingRemainingTime = view.findViewById(R.id.trendingRemainingTime);
        bannerImageView = view.findViewById(R.id.bannerImageView);
        newArrivalsViewAll = view.findViewById(R.id.newArrivalsViewAll);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerProgressBar = view.findViewById(R.id.bannerProgressBar);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        Log.d(TAG, "Views initialized");

        if (getContext() == null) {
            Log.e(TAG, "Context is null, fragment not attached to activity");
            Toast.makeText(requireContext(), "Lỗi: Fragment không gắn với Activity", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Initializing ApiManager");
        apiManager = new ApiManager(getContext());

        // Search listener
        Log.d(TAG, "Setting search listener");
        ivSearch.setOnClickListener(v -> {
            Log.d(TAG, "ivSearch clicked");
            if (getActivity() instanceof MainActivity) {
                String query = etSearch.getText().toString().trim();
                Log.d(TAG, "Search query: " + query);
                ((MainActivity) getActivity()).switchToSearchWithData(query);
            } else {
                Log.e(TAG, "Activity is not MainActivity");
                Toast.makeText(getContext(), "Không thể thực hiện tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        // Khởi tạo danh mục
        Log.d(TAG, "Initializing category adapter");
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
        categoryRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Khởi tạo banner
        Log.d(TAG, "Initializing banner adapter");
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, this::handleBannerClick);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setOffscreenPageLimit(3);
        bannerIndicator.setViewPager(bannerViewPager);

        // Khởi tạo Deal of the Day
        Log.d(TAG, "Initializing deal adapter");
        dealList = new ArrayList<>();
//        dealAdapter = new ProductAdapter(getContext(), dealList, "deal");
        dealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        dealRecyclerView.setAdapter(dealAdapter);
        dealViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DealActivity.class);
            startActivity(intent);
        });
        startDealCountDownTimer();

        // Khởi tạo Special Offers
        Log.d(TAG, "Initializing special offers adapter");
        specialOffersList = new ArrayList<>();
//        specialOffersAdapter = new ProductAdapter(getContext(), specialOffersList, "special_offers");
        specialOffersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        specialOffersRecyclerView.setAdapter(specialOffersAdapter);

        // Khởi tạo Trending Products
        Log.d(TAG, "Initializing trending adapter");
        trendingList = new ArrayList<>();
//        trendingAdapter = new ProductAdapter(getContext(), trendingList, "trending");
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(trendingAdapter);
        trendingViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DealActivity.class);
            startActivity(intent);
        });
        startTrendingCountDownTimer();

        // New Arrivals
        Log.d(TAG, "Setting new arrivals");
        bannerImageView.setImageResource(R.drawable.new_arrivals_image);
        newArrivalsViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewArrivalsActivity.class);
            startActivity(intent);
        });

        // Tải dữ liệu
        Log.d(TAG, "Calling loadBanners");
        loadBanners();
        Log.d(TAG, "Calling loadCategories");
        loadCategories();
    }

    private void loadBanners() {
        Log.d(TAG, "Starting loadBanners");
        if (bannerProgressBar == null) {
            Log.e(TAG, "bannerProgressBar is null");
            Toast.makeText(getContext(), "Lỗi giao diện: bannerProgressBar không tìm thấy", Toast.LENGTH_SHORT).show();
            return;
        }
        bannerProgressBar.setVisibility(View.VISIBLE);
        apiManager.getBanners(new ApiResponseListener<BannerResponse>() {
            @Override
            public void onSuccess(BannerResponse response) {
                Log.d(TAG, "Banner response received: " + new Gson().toJson(response));
                bannerProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getBanners() != null) {
                    bannerList.clear();
                    for (Banner banner : response.getMetadata().getBanners()) {
                        Log.d(TAG, "Processing banner: id=" + banner.getId() + ", status=" + banner.getStatus());
                        if ("active".equals(banner.getStatus())) {
                            bannerList.add(banner);
                        }
                    }
                    Log.d(TAG, "Filtered bannerList size: " + bannerList.size());
                    bannerAdapter.notifyDataSetChanged();
                    bannerIndicator.createIndicators(bannerList.size(), 0);
                    bannerViewPager.setVisibility(bannerList.isEmpty() ? View.GONE : View.VISIBLE);
                    if (!bannerList.isEmpty()) {
                        startBannerAutoSlide();
                    }
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
        String linkTarget = banner.getLinkTarget();
        String slug = extractSlugFromLink(linkTarget);
        Log.d(TAG, "Banner clicked: linkType=" + linkType + ", slug=" + slug);
        if ("campaign".equals(linkType)) {
            Intent intent = new Intent(getContext(), CampaignActivity.class);
            intent.putExtra("campaignSlug", slug);
            startActivity(intent);
        } else if ("shop".equals(linkType)) {
            Intent intent = new Intent(getContext(), ShopActivity.class);
            intent.putExtra("shopSlug", slug);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Loại liên kết không hỗ trợ", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractSlugFromLink(String linkTarget) {
        if (linkTarget == null || linkTarget.isEmpty()) return "";
        String[] parts = linkTarget.split("/");
        return parts[parts.length - 1];
    }

    private void loadCategories() {
        categoryProgressBar.setVisibility(View.VISIBLE);
        apiManager.getCategories(new ApiResponseListener<CategoryResponse>() {
            @Override
            public void onSuccess(CategoryResponse response) {
                categoryProgressBar.setVisibility(View.GONE);
                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
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
        if (bannerAutoSlideTimer != null) {
            bannerAutoSlideTimer.cancel();
        }
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
                if (isAdded()) {
                    startBannerAutoSlide();
                }
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
                if (isAdded()) {
                    dealRemainingTime.setText("00:00:00");
                }
            }
        }.start();
    }

    private void startTrendingCountDownTimer() {
        Calendar endTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        long millisUntilFinished = endTime.getTimeInMillis() - System.currentTimeMillis();
        if (millisUntilFinished <= 0) millisUntilFinished = 0;

        trendingCountDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAdded()) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    seconds %= 60;
                    minutes %= 60;
                    trendingRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) {
                    trendingRemainingTime.setText("00:00:00");
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dealCountDownTimer != null) dealCountDownTimer.cancel();
        if (trendingCountDownTimer != null) trendingCountDownTimer.cancel();
        if (bannerAutoSlideTimer != null) bannerAutoSlideTimer.cancel();
        Log.d(TAG, "onDestroyView called");
    }
}