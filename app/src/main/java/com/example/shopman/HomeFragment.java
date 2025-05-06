package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivVoiceSearch;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerDots;
    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private RecyclerView dealRecyclerView, trendingRecyclerView;
    private ProductAdapter dealAdapter, trendingAdapter;
    private List<Product> dealList, trendingList;
    private TextView dealViewAll, trendingViewAll, newArrivalsViewAll;
    private Button visitNowButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        return inflater.inflate(R.layout.activity_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etSearch = view.findViewById(R.id.etSearch);
        ivVoiceSearch = view.findViewById(R.id.ivVoiceSearch);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerDots = view.findViewById(R.id.bannerDots);
        dealRecyclerView = view.findViewById(R.id.dealRecyclerView);
        trendingRecyclerView = view.findViewById(R.id.trendingRecyclerView);
        dealViewAll = view.findViewById(R.id.dealViewAll);
        trendingViewAll = view.findViewById(R.id.trendingViewAll);
        newArrivalsViewAll = view.findViewById(R.id.newArrivalsViewAll);
        visitNowButton = view.findViewById(R.id.visitNowButton);

        // Set up search bar
        ivVoiceSearch.setOnClickListener(v -> Toast.makeText(getActivity(), "Voice Search clicked", Toast.LENGTH_SHORT).show());


        // Set up RecyclerView for categories
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Beauty", R.drawable.ic_beauty));
        categoryList.add(new Category("Fashion", R.drawable.ic_fashion));
        categoryList.add(new Category("Kids", R.drawable.ic_kids));
        categoryList.add(new Category("Men", R.drawable.ic_mens));
        categoryList.add(new Category("Women", R.drawable.ic_womens));

        categoryAdapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up ViewPager2 for banners
        bannerList = new ArrayList<>();
        bannerList.add(new Banner(R.drawable.banner_image));
        bannerList.add(new Banner(R.drawable.banner_image));
        bannerList.add(new Banner(R.drawable.banner_image));

        bannerAdapter = new BannerAdapter(bannerList, () -> {
            Toast.makeText(getActivity(), "Shop Now clicked", Toast.LENGTH_SHORT).show();
        });
        bannerViewPager.setAdapter(bannerAdapter);

        new TabLayoutMediator(bannerDots, bannerViewPager, (tab, position) -> {}).attach();

        // Set up RecyclerView for Deal of the Day
        dealList = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        sizes.add("6 UK");
        sizes.add("7 UK");
        sizes.add("8 UK");
        sizes.add("9 UK");
        sizes.add("10 UK");
        dealList.add(new Product("Women Printed Kurta", "Neque porro quisquam est qui", "₹1500", R.drawable.deal_image_1, 4.5f, sizes, "A beautiful printed kurta for women, perfect for casual wear."));
        dealList.add(new Product("HRX by Hrithik Roshan", "Neque porro quisquam est qui", "₹2499", R.drawable.deal_image_2, 4.0f, sizes, "A stylish and comfortable outfit by HRX, designed for active lifestyles."));

        dealAdapter = new ProductAdapter(dealList);
        dealRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        dealRecyclerView.setAdapter(dealAdapter);

        dealViewAll.setOnClickListener(v -> Toast.makeText(getActivity(), "Deal View All clicked", Toast.LENGTH_SHORT).show());

        // Set up Special Offers
        visitNowButton.setOnClickListener(v -> Toast.makeText(getActivity(), "Visit Now clicked", Toast.LENGTH_SHORT).show());

        // Set up RecyclerView for Trending Products
        trendingList = new ArrayList<>();
        trendingList.add(new Product("IMC Smartwatch", "SHH 2019 2014 44mm", "₹2500", R.drawable.trending_image_1, 4.2f, sizes, "A smartwatch with advanced features for fitness tracking."));
        trendingList.add(new Product("Labbin White", "For Men and Female", "₹1500", R.drawable.trending_image_2, 3.8f, sizes, "A versatile white shirt suitable for both men and women."));

        trendingAdapter = new ProductAdapter(trendingList);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(trendingAdapter);

        trendingViewAll.setOnClickListener(v -> Toast.makeText(getActivity(), "Trending View All clicked", Toast.LENGTH_SHORT).show());

        newArrivalsViewAll.setOnClickListener(v -> Toast.makeText(getActivity(), "New Arrivals View All clicked", Toast.LENGTH_SHORT).show());
    }
}
