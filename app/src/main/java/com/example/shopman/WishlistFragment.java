package com.example.shopman;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    private EditText etSearch;
    private ImageView ivVoiceSearch;
    private TextView itemCount;
    private Button sortButton, filterButton;
    private RecyclerView wishlistRecyclerView;
    private ProductAdapter wishlistAdapter;
    private List<Product> wishlistItems;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Đảm bảo đúng layout của Fragment
        View view = inflater.inflate(R.layout.activity_wishlist, container, false);

        // Ánh xạ các view
        etSearch = view.findViewById(R.id.etSearch);
        ivVoiceSearch = view.findViewById(R.id.ivVoiceSearch);
        itemCount = view.findViewById(R.id.itemCount);
        sortButton = view.findViewById(R.id.sortButton);
        filterButton = view.findViewById(R.id.filterButton);
        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView);

        // Xử lý sự kiện search
        ivVoiceSearch.setOnClickListener(v -> Toast.makeText(getContext(), "Voice Search clicked", Toast.LENGTH_SHORT).show());

        // Xử lý sự kiện Sort và Filter
        sortButton.setOnClickListener(v -> Toast.makeText(getContext(), "Sort button clicked", Toast.LENGTH_SHORT).show());
        filterButton.setOnClickListener(v -> Toast.makeText(getContext(), "Filter button clicked", Toast.LENGTH_SHORT).show());

        // Cấu hình RecyclerView
        wishlistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Khởi tạo danh sách sản phẩm yêu thích
        wishlistItems = new ArrayList<>();
        loadWishlistData();

        // Gán Adapter
        wishlistAdapter = new ProductAdapter(wishlistItems);
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        // Cập nhật số lượng sản phẩm
        itemCount.setText(wishlistItems.size() + " Items");

        return view;
    }

    private void loadWishlistData() {
        List<String> sizes = new ArrayList<>();
        sizes.add("6 UK");
        sizes.add("7 UK");
        sizes.add("8 UK");
        sizes.add("9 UK");
        sizes.add("10 UK");

        wishlistItems.add(new Product("Black Winter Jacket", "Autumn And Winter Casual Cotton padded jacket", "₹499", R.drawable.trending_image_1, 4.0f, sizes, "A warm and stylish jacket for winter."));
        wishlistItems.add(new Product("Mens Starry Shirt", "100% Cotton Fabric", "₹999", R.drawable.trending_image_2, 4.5f, sizes, "A starry-patterned shirt made of 100% cotton."));
        wishlistItems.add(new Product("Black Dress", "Solid Black Dress For Women, Sexy Chain Shorts Ladi...", "₹2000", R.drawable.trending_image_1, 4.0f, sizes, "A chic black dress with chain shorts for women."));
        wishlistItems.add(new Product("Pink Embroidered Dress", "Earthen Rose Pink Embroidered Tiered Max...", "₹1900", R.drawable.trending_image_2, 4.5f, sizes, "A beautiful pink embroidered tiered maxi dress."));
        wishlistItems.add(new Product("Flare Dress", "Anthaea Black & Rust Orange Floral Print Tiered Midi F...", "₹1990", R.drawable.deal_image_1, 4.0f, sizes, "A floral print midi dress with a flared design."));
        wishlistItems.add(new Product("Denim Dress", "Casual Denim Dress", "₹1500", R.drawable.deal_image_2, 4.0f, sizes, "A casual denim dress for everyday wear."));

        // Cập nhật RecyclerView nếu Adapter đã được gán
        if (wishlistAdapter != null) {
            wishlistAdapter.notifyDataSetChanged();
        }
    }
}
