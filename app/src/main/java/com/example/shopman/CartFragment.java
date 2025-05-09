package com.example.shopman;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private SharedPreferences sharedPreferences;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment, container, false);

        // Initialize views
        tvAddress = view.findViewById(R.id.tvAddress);
        tvContact = view.findViewById(R.id.tvContact);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Load saved address and contact
        loadAddressAndContact();

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

        // Add click listener to tvAddress to edit address
        tvAddress.setOnClickListener(v -> showEditAddressDialog());

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

    private void loadAddressAndContact() {
        String savedAddress = sharedPreferences.getString("address", "Address: 216 St Paul’s Rd, London N1 2LL, UK");
        String savedContact = sharedPreferences.getString("contact", "Contact: +44-7848232");
        tvAddress.setText(savedAddress);
        tvContact.setText(savedContact);
    }

    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);
        EditText etCountry = dialogView.findViewById(R.id.etCountry);
        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etDetailedAddress = dialogView.findViewById(R.id.etDetailedAddress);
        EditText etContact = dialogView.findViewById(R.id.etContact);
        Button btnUpdateAddress = dialogView.findViewById(R.id.btnUpdateAddress);

        // Pre-fill with current values
        String[] addressParts = tvAddress.getText().toString().replace("Address: ", "").split(", ");
        if (addressParts.length >= 3) {
            etCountry.setText(addressParts[0]);
            etCity.setText(addressParts[1]);
            etDetailedAddress.setText(addressParts[2]);
        }
        etContact.setText(tvContact.getText().toString().replace("Contact: ", ""));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnUpdateAddress.setOnClickListener(v -> {
            String country = etCountry.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String detailedAddress = etDetailedAddress.getText().toString().trim();
            String contact = etContact.getText().toString().trim();

            if (country.isEmpty() || city.isEmpty() || detailedAddress.isEmpty() || contact.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Format address and contact
            String fullAddress = "Address: " + country + ", " + city + ", " + detailedAddress;
            String fullContact = "Contact: " + contact;

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("address", fullAddress);
            editor.putString("contact", fullContact);
            editor.apply();

            // Update UI
            tvAddress.setText(fullAddress);
            tvContact.setText(fullContact);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onItemSelectionChanged() {
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