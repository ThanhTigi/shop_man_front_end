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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
    private List<WishlistProductDetail> dealList; // Sửa thành WishlistProductDetail
    private TextView dealViewAll;
    private TextView dealRemainingTime;
    private RecyclerView specialOffersRecyclerView;
    private ProductAdapter specialOffersAdapter;
    private List<WishlistProductDetail> specialOffersList; // Sửa thành WishlistProductDetail
    private RecyclerView trendingRecyclerView;
    private ProductAdapter trendingAdapter;
    private List<WishlistProductDetail> trendingList; // Sửa thành WishlistProductDetail
    private TextView trendingViewAll;
    private TextView trendingRemainingTime;
    private ImageView bannerImageView;
    private TextView newArrivalsViewAll;
    private ViewPager2 bannerViewPager;
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

        Log.d(TAG, "Views initialized");

        if (getContext() == null) {
            Log.e(TAG, "Context is null, fragment not attached to activity");
            return;
        }

        apiManager = new ApiManager(getContext());

        // Search listener
        ivSearch.setOnClickListener(v -> {
            Log.d(TAG, "ivSearch clicked");
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
        categoryRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Khởi tạo banner
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, this::handleBannerClick);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setOffscreenPageLimit(3);

        // Khởi tạo Deal of the Day
//        dealList = new ArrayList<>();
//        dealAdapter = new ProductAdapter(dealList);
//        dealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        dealRecyclerView.setAdapter(dealAdapter);
//        dealViewAll.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), DealActivity.class);
//            startActivity(intent);
//        });
        startDealCountDownTimer();

        // Khởi tạo Special Offers
//        specialOffersList = new ArrayList<>();
//        specialOffersAdapter = new ProductAdapter(specialOffersList);
//        specialOffersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        specialOffersRecyclerView.setAdapter(specialOffersAdapter);

        // Khởi tạo Trending Products
//        trendingList = new ArrayList<>();
//        trendingAdapter = new ProductAdapter(trendingList);
//        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        trendingRecyclerView.setAdapter(trendingAdapter);
//        trendingViewAll.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), TrendingProductActivity.class);
//            startActivity(intent);
//        });
        startTrendingCountDownTimer();

        // New Arrivals
        bannerImageView.setImageResource(R.drawable.new_arrivals_image);
        newArrivalsViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewArrivalsActivity.class);
            startActivity(intent);
        });

        // Tải dữ liệu
        loadBanners();
        loadCategories();
//        loadDeals();
//        loadSpecialOffers();
//        loadTrendingProducts();
        startBannerAutoSlide();
    }

    private void loadBanners() {
        apiManager.getBanners(new ApiResponseListener<BannerResponse>() {
            @Override
            public void onSuccess(BannerResponse response) {
                if (response != null && response.getMetadata() != null && response.getMetadata().getBanners() != null) {
                    bannerList.clear();
                    for (Banner banner : response.getMetadata().getBanners()) {
                        if ("active".equals(banner.getStatus())) {
                            bannerList.add(banner);
                        }
                    }
                    bannerAdapter.notifyDataSetChanged();
                    bannerViewPager.setVisibility(bannerList.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "Không thể tải banner", Toast.LENGTH_SHORT).show();
                    bannerViewPager.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to load banners: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải banner", Toast.LENGTH_SHORT).show();
                bannerViewPager.setVisibility(View.GONE);
            }
        });
    }

    private void handleBannerClick(Banner banner) {
        String linkType = banner.getLinkType();
        String linkTarget = banner.getLinkTarget();
        if ("campaign".equals(linkType)) {
            String campaignSlug = extractSlugFromLink(linkTarget);
            Intent intent = new Intent(getContext(), CampaignActivity.class);
            intent.putExtra("campaignSlug", campaignSlug);
            startActivity(intent);
        } else if ("shop".equals(linkType)) {
            String shopSlug = extractSlugFromLink(linkTarget);
//            Intent intent = new Intent(getContext(), ShopActivity.class);
//            intent.putExtra("shopSlug", shopSlug);
//            startActivity(intent);
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
                    loadFallbackCategories();
                }
            }

            @Override
            public void onError(String errorMessage) {
                categoryProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                loadFallbackCategories();
            }
        });
    }

//    private void loadDeals() {
//        apiManager.getDealOfTheDayProducts(1, PAGE_SIZE, new ApiResponseListener<DealResponse>() {
//            @Override
//            public void onSuccess(DealResponse response) {
//                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
//                    List<DealProduct> dealProducts = response.getMetadata().getMetadata().getProducts();
//                    dealList.clear();
//                    for (DealProduct dp : dealProducts) {
//                        WishlistProductDetail product = new WishlistProductDetail();
//                        product.setId(String.valueOf(dp.getId()));
//                        product.setName(dp.getName());
//                        product.setDesc(dp.getDesc());
//                        product.setDescPlain(dp.getDescPlain());
//                        product.setPrice(dp.getPrice());
//                        product.setThumb(dp.getThumb());
//                        product.setRating(dp.getRating());
//                        product.setDiscountPercentage(dp.getDiscountPercentage());
//                        product.setSlug(dp.getSlug());
//                        product.setCategoryId(dp.getCategoryId());
//                        product.setShopId(dp.getShopId());
//                        product.setSaleCount(dp.getSaleCount());
//                        product.setCreatedAt(dp.getCreatedAt());
//                        dealList.add(product);
//                    }
//                    dealAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(getContext(), "Không tìm thấy deal", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Log.e(TAG, "Failed to load deals: " + errorMessage);
//                Toast.makeText(getContext(), "Lỗi tải deal", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void loadSpecialOffers() {
//        // Tạm thời sử dụng cùng API với Deal of the Day, bạn có thể thay bằng API khác
//        apiManager.getDealOfTheDayProducts(1, PAGE_SIZE, new ApiResponseListener<DealResponse>() {
//            @Override
//            public void onSuccess(DealResponse response) {
//                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
//                    List<DealProduct> dealProducts = response.getMetadata().getMetadata().getProducts();
//                    specialOffersList.clear();
//                    for (DealProduct dp : dealProducts) {
//                        WishlistProductDetail product = new WishlistProductDetail();
//                        product.setId(String.valueOf(dp.getId()));
//                        product.setName(dp.getName());
//                        product.setDesc(dp.getDesc());
//                        product.setDescPlain(dp.getDescPlain());
//                        product.setPrice(dp.getPrice());
//                        product.setThumb(dp.getThumb());
//                        product.setRating(dp.getRating());
//                        product.setDiscountPercentage(dp.getDiscountPercentage());
//                        product.setSlug(dp.getSlug());
//                        product.setCategoryId(dp.getCategoryId());
//                        product.setShopId(dp.getShopId());
//                        product.setSaleCount(dp.getSaleCount());
//                        product.setCreatedAt(dp.getCreatedAt());
//                        specialOffersList.add(product);
//                    }
//                    specialOffersAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Log.e(TAG, "Failed to load special offers: " + errorMessage);
//            }
//        });
//    }

//    private void loadTrendingProducts() {
//        // Tạm thời sử dụng cùng API với Deal of the Day, bạn có thể thay bằng API khác
//        apiManager.getDealOfTheDayProducts(1, PAGE_SIZE, new ApiResponseListener<DealResponse>() {
//            @Override
//            public void onSuccess(DealResponse response) {
//                if (response != null && response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
//                    List<DealProduct> dealProducts = response.getMetadata().getMetadata().getProducts();
//                    trendingList.clear();
//                    for (DealProduct dp : dealProducts) {
//                        WishlistProductDetail product = new WishlistProductDetail();
//                        product.setId(String.valueOf(dp.getId()));
//                        product.setName(dp.getName());
//                        product.setDesc(dp.getDesc());
//                        product.setDescPlain(dp.getDescPlain());
//                        product.setPrice(dp.getPrice());
//                        product.setThumb(dp.getThumb());
//                        product.setRating(dp.getRating());
//                        product.setDiscountPercentage(dp.getDiscountPercentage());
//                        product.setSlug(dp.getSlug());
//                        product.setCategoryId(dp.getCategoryId());
//                        product.setShopId(dp.getShopId());
//                        product.setSaleCount(dp.getSaleCount());
//                        product.setCreatedAt(dp.getCreatedAt());
//                        trendingList.add(product);
//                    }
//                    trendingAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onError(String errorMessage) {
//                Log.e(TAG, "Failed to load trending products: " + errorMessage);
//            }
//        });
//    }

    private void loadFallbackCategories() {
        categoryList.clear();
        categoryList.add(new Category("Beauty", "https://example.com/beauty.png", "beauty"));
        categoryList.add(new Category("Fashion", "https://example.com/fashion.png", "fashion"));
        categoryList.add(new Category("Kids", "https://example.com/kids.png", "kids"));
        categoryList.add(new Category("Men", "https://example.com/mens.png", "mens"));
        categoryList.add(new Category("Women", "https://example.com/womens.png", "womens"));
        categoryAdapter.notifyDataSetChanged();
    }

    private void startBannerAutoSlide() {
        if (bannerAutoSlideTimer != null) {
            bannerAutoSlideTimer.cancel();
        }
        bannerAutoSlideTimer = new CountDownTimer(Long.MAX_VALUE, 5000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (bannerList.size() > 0) {
                    int currentItem = bannerViewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerList.size();
                    bannerViewPager.setCurrentItem(nextItem, true);
                }
            }

            @Override
            public void onFinish() {
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
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                seconds %= 60;
                minutes %= 60;
                dealRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                dealRemainingTime.setText("00:00:00");
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
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                seconds %= 60;
                minutes %= 60;
                trendingRemainingTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                trendingRemainingTime.setText("00:00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dealCountDownTimer != null) dealCountDownTimer.cancel();
        if (trendingCountDownTimer != null) trendingCountDownTimer.cancel();
        if (bannerAutoSlideTimer != null) bannerAutoSlideTimer.cancel();
    }
}