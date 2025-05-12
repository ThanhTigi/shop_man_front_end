package com.example.shopman.fragments.wishlist;

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

import com.example.shopman.Product;
import com.example.shopman.ProductAdapter;
import com.example.shopman.R;
import com.example.shopman.utilitis.ProductsConst;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivSearch;
    private TextView itemCount;
    private RecyclerView wishlistRecyclerView;
    private ProductAdapter wishlistAdapter;
    private List<Product> totalItems;
    private List<Product> wishlistItems;

    public WishlistFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        itemCount = view.findViewById(R.id.itemCount);
        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView);

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("====== clicked");
                searchItem();
            }
        });

        totalItems = new ArrayList<>();
        totalItems.add(ProductsConst.totalProducts.get(17));
        totalItems.add(ProductsConst.totalProducts.get(5));
        totalItems.add(ProductsConst.totalProducts.get(7));
        totalItems.add(ProductsConst.totalProducts.get(9));
        totalItems.add(ProductsConst.totalProducts.get(11));
        totalItems.add(ProductsConst.totalProducts.get(13));
        totalItems.add(ProductsConst.totalProducts.get(15));

        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        wishlistItems = new ArrayList<>();
        loadWishlistData();

        wishlistAdapter = new ProductAdapter(wishlistItems);
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        itemCount.setText(wishlistItems.size() + " Items");

        return view;
    }

    private void loadWishlistData() {

        wishlistItems.add(ProductsConst.totalProducts.get(17));
        wishlistItems.add(ProductsConst.totalProducts.get(5));
        wishlistItems.add(ProductsConst.totalProducts.get(7));
        wishlistItems.add(ProductsConst.totalProducts.get(9));
        wishlistItems.add(ProductsConst.totalProducts.get(11));
        wishlistItems.add(ProductsConst.totalProducts.get(13));
        wishlistItems.add(ProductsConst.totalProducts.get(15));

        if (wishlistAdapter != null) {
            wishlistAdapter.notifyDataSetChanged();
        }
    }

    private void searchItem()
    {
        List<Product> result = new ArrayList<>();

        if (etSearch.getText().toString().toLowerCase().trim().isEmpty()) {
            result = totalItems;
        }
        else
        {
            for (Product product : totalItems) {
                if (product.getName().toLowerCase().contains(etSearch.getText().toString().toLowerCase())) {
                    result.add(product);
                }
            }
        }

        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        wishlistAdapter = new ProductAdapter(result);
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        itemCount.setText(result.size() + " Items");
    }


}
