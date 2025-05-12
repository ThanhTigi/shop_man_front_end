package com.example.shopman;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivVoiceSearch;
    private TextView itemCount;
    private RecyclerView searchRecyclerView;
    private ProductAdapter searchAdapter;


    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        etSearch = view.findViewById(R.id.etSearch);
        ivVoiceSearch = view.findViewById(R.id.ivSearch);
        itemCount = view.findViewById(R.id.itemCount);

        ivVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchProduct(etSearch.getText().toString());

            }
        });

        if (AppConfig.isSearch) {
            String keywordSearch = AppConfig.keywordSearch;
            AppConfig.isSearch = false;
            etSearch.setText(AppConfig.keywordSearch);
            searchProduct(keywordSearch);
        }
        else
        {
            searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            searchAdapter = new ProductAdapter(ProductsConst.totalProducts);
            searchRecyclerView.setAdapter(searchAdapter);
            itemCount.setText(ProductsConst.totalProducts.size() + " Items");
        }

        return view;
    }

    private void searchProduct(String keyword)
    {
        List<Product> result = ProductsConst.searchProductsByName(keyword);
        searchRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchAdapter = new ProductAdapter(result);
        searchRecyclerView.setAdapter(searchAdapter);

        itemCount.setText(result.size() + " Items");
    }

    public void searchWithKeyword(String keyword) {
        searchProduct(keyword);
    }

}
