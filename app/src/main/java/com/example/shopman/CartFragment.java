package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {

    private TextView tvAddress, tvContact, tvTotalAmount;
    private CheckBox cbSelectAll;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button btnCheckout;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        // Initialize views
        tvAddress = view.findViewById(R.id.tvAddress);
        tvContact = view.findViewById(R.id.tvContact);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Set up cart items
        cartItems = new ArrayList<>();
        List<String> sizes = new ArrayList<>();
        sizes.add("6 UK");
        sizes.add("7 UK");
        sizes.add("8 UK");
        sizes.add("9 UK");
        sizes.add("10 UK");

        Product product1 = new Product("Women's Casual Wear", "Floral Dress", "$34.00", R.drawable.trending_image_1, 4.8f, sizes, "A beautiful floral dress for women.");
        Product product2 = new Product("Men's Jacket", "Casual Jacket", "$45.00", R.drawable.trending_image_2, 4.7f, sizes, "A stylish jacket for men.");

        cartItems.add(new CartItem(product1, 1, false, "Black"));
        cartItems.add(new CartItem(product2, 1, false, "Green"));

        // Set up RecyclerView
        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerView.setAdapter(cartAdapter);

        // Set up Select All checkbox
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
        });

        // Inside btnCheckout.setOnClickListener in CartFragment.java
        btnCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(getActivity(), "Please select at least one item to checkout", Toast.LENGTH_SHORT).show();
            } else {
                Intent checkoutIntent = new Intent(getActivity(), CheckoutActivity.class);
                checkoutIntent.putExtra("selectedCartItems", new ArrayList<>(selectedItems));
                startActivity(checkoutIntent);
            }
        });

        // Initial total amount update
        updateTotalAmount();

        return view;
    }

    @Override
    public void onItemSelectionChanged() {
        // Update Select All checkbox state
        boolean allSelected = true;
        for (CartItem item : cartItems) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        cbSelectAll.setChecked(allSelected);
        updateTotalAmount();
    }

    @Override
    public void onQuantityChanged() {
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getTotalPrice();
            }
        }
        tvTotalAmount.setText("$" + String.format("%.2f", total));
    }
}
