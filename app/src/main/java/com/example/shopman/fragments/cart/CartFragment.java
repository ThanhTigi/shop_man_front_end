package com.example.shopman.fragments.cart;

import android.content.Intent;
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

import com.example.shopman.R;
import com.example.shopman.utilitis.MyPreferences;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        tvAddress = view.findViewById(R.id.tvAddress);
        tvContact = view.findViewById(R.id.tvContact);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAddressAndContact();

        CartProducts cartProducts;

        if (MyPreferences.getString(getContext(),"cart_products","").isEmpty())
        {
            cartProducts = new CartProducts(new ArrayList<>());
        }
        else
        {
            cartProducts = CartProducts.fromJson(MyPreferences.getString(getContext(),"cart_products",""));
        }

        cartItems = cartProducts.getProducts();

        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerView.setAdapter(cartAdapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked)
            {
                return;
            }
            for (CartItem item : cartItems) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
        });

//        onItemSelectionChanged();

        tvAddress.setOnClickListener(v -> showEditAddressDialog());

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

        updateTotalAmount();
    }


    private void loadAddressAndContact() {
        String savedAddress = MyPreferences.getString(getContext(),"user_address","216 St Paul's Rd, London N1 2LL, UK");
        String savedContact = MyPreferences.getString(getContext(),"user_phone_number", "0326749576");
        tvAddress.setText("Address: " + savedAddress);
        tvContact.setText("Contact: " + savedContact);
    }

    private void showEditAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);
        EditText etCountry = dialogView.findViewById(R.id.etCountry);
        EditText etCity = dialogView.findViewById(R.id.etCity);
        EditText etDetailedAddress = dialogView.findViewById(R.id.etDetailedAddress);
        EditText etContact = dialogView.findViewById(R.id.etContact);
        Button btnUpdateAddress = dialogView.findViewById(R.id.btnUpdateAddress);

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

            if (!contact.matches("\\d{10}")) {
                Toast.makeText(getActivity(), "Contact must be a 10-digit phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = country + ", " + city + ", " + detailedAddress;
            String fullContact = contact;

            MyPreferences.setString(getContext(),"user_address",fullAddress);
            MyPreferences.setString(getContext(),"user_phone_number",fullContact);

            tvAddress.setText("Address: " + fullAddress);
            tvContact.setText("Contact: " + fullContact);
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

        CartProducts cartProducts = new CartProducts(cartItems);
        MyPreferences.setString(getContext(),"cart_products",cartProducts.toJson());

        tvTotalAmount.setText("đ" + String.format("%.2f", total));
    }
}