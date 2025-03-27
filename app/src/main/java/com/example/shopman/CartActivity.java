package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemChangeListener {

    private ImageView ivBack;
    private TextView tvAddress, tvContact, tvTotalAmount;
    private CheckBox cbSelectAll;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        ivBack = findViewById(R.id.ivBack);
        tvAddress = findViewById(R.id.tvAddress);
        tvContact = findViewById(R.id.tvContact);
        cbSelectAll = findViewById(R.id.cbSelectAll);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Set up back button
        ivBack.setOnClickListener(v -> finish());

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
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        // Set up Select All checkbox
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
        });

        // Inside btnCheckout.setOnClickListener in CartActivity.java
        btnCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please select at least one item to checkout", Toast.LENGTH_SHORT).show();
            } else {
                Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
                checkoutIntent.putExtra("selectedCartItems", new ArrayList<>(selectedItems));
                startActivity(checkoutIntent);
            }
        });

        // Initial total amount update
        updateTotalAmount();
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