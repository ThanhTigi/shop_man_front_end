package com.example.shopman.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private ImageView bannerImageView;
    private RecyclerView  trendingRecyclerView;
    private ProductAdapter trendingAdapter;
    private List<Product> trendingList;
    private TextView trendingViewAll;
    private RecyclerView productRecyclerView;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        trendingRecyclerView = view.findViewById(R.id.trendingRecyclerView);
        trendingViewAll = view.findViewById(R.id.trendingViewAll);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        bannerImageView = view.findViewById(R.id.bannerImageView);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToSearchWithData(etSearch.getText().toString());
                }
            }});


        categoryList = new ArrayList<>();
        categoryList.add(new Category("Beauty", R.drawable.ic_beauty));
        categoryList.add(new Category("Fashion", R.drawable.ic_fashion));
        categoryList.add(new Category("Kids", R.drawable.ic_kids));
        categoryList.add(new Category("Men", R.drawable.ic_mens));
        categoryList.add(new Category("Women", R.drawable.ic_womens));


        categoryAdapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        bannerImageView.setImageResource(R.drawable.banner_image);

        trendingList = new ArrayList<>();
        trendingList.add(ProductsConst.totalProducts.get(0));
        trendingList.add(ProductsConst.totalProducts.get(1));
        trendingList.add(ProductsConst.totalProducts.get(3));
        trendingList.add(ProductsConst.totalProducts.get(4));
        trendingList.add(ProductsConst.totalProducts.get(5));
        trendingAdapter = new ProductAdapter(trendingList);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(trendingAdapter);

        trendingViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TrendingProductActivity.class);
                startActivity(intent);
            }
        });

        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        ProductAdapter productAdapter = new ProductAdapter(ProductsConst.totalProducts);
        productRecyclerView.setAdapter(productAdapter);
    }
}
