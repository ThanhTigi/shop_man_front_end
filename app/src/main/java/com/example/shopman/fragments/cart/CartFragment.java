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
import com.example.shopman.activities.LoginActivity;
import com.example.shopman.adapters.CartAdapter;
import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;
import com.example.shopman.models.cart.CartItemResponse;
import com.example.shopman.models.cart.CartProducts;
import com.example.shopman.models.cart.CartResponse;
import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;
import com.example.shopman.utilitis.MyPreferences;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private TextView tvAddress, tvContact, tvTotalAmount;
    private CheckBox cbSelectAll;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartItems;
    private Button btnCheckout;
    private ApiManager apiManager;

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

        apiManager = new ApiManager(requireContext());
        cartItems = new ArrayList<>();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAddressAndContact();
        loadCartFromApi();

        cartAdapter = new CartAdapter(requireContext(), cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartRecyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnCartItemChangeListener(new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onItemSelectionChanged(Product product, boolean isSelected) {
                product.setSelected(isSelected);
                updateSelectAllState();
                updateTotalAmount();
                saveCart();
            }

            @Override
            public void onQuantityChanged(Product product, int quantity) {
                product.setQuantity(quantity);
                updateTotalAmount();
                saveCart();
            }

            @Override
            public void onRemoveItem(Product product) {
                cartItems.remove(product);
                cartAdapter.notifyDataSetChanged();
                updateSelectAllState();
                updateTotalAmount();
                saveCart();
            }
        });

        cbSelectAll.setOnClickListener(v -> {
            boolean isChecked = cbSelectAll.isChecked();
            for (Product item : cartItems) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
            saveCart();
        });

        tvAddress.setOnClickListener(v -> showEditAddressDialog());

        btnCheckout.setOnClickListener(v -> {
            List<Product> selectedItems = new ArrayList<>();
            for (Product item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(getActivity(), "Please select at least one item to checkout", Toast.LENGTH_SHORT).show();
            } else {
//                Intent checkoutIntent = new Intent(getActivity(), CheckoutActivity.class);
//                checkoutIntent.putParcelableArrayListExtra("selectedCartItems", new ArrayList<>(selectedItems));
//                startActivity(checkoutIntent);
            }
        });
    }

    private void loadCartFromApi() {
        apiManager.getCart(new ApiResponseListener<CartResponse>() {
            @Override
            public void onSuccess(CartResponse response) {
                cartItems.clear();
                if (response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    for (CartItemResponse item : response.getMetadata().getMetadata()) {
                        Product product = item.toProduct();
                        if (product.getPrice() == 0) {
                            product.setPrice(getFallbackPrice(item.getProductId()));
                        }
                        cartItems.add(product);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateSelectAllState();
                updateTotalAmount();
                saveCart();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                loadCartFromPreferences();
            }
        });
    }

    private void loadCartFromPreferences() {
        CartProducts cartProducts;
        if (MyPreferences.getString(getContext(), "cart_products", "").isEmpty()) {
            cartProducts = new CartProducts(new ArrayList<>());
        } else {
            cartProducts = CartProducts.fromJson(MyPreferences.getString(getContext(), "cart_products", ""));
        }
        cartItems.clear();
        if (cartProducts.getProducts() != null) {
            cartItems.addAll(cartProducts.getProducts());
        }
        cartAdapter.notifyDataSetChanged();
        updateSelectAllState();
        updateTotalAmount();
    }

    private long getFallbackPrice(int productId) {
        return 100000L; // Giá tạm thời
    }

    private void loadAddressAndContact() {
        String savedAddress = MyPreferences.getString(getContext(), "user_address", "216 St Paul's Rd, London N1 2LL, UK");
        String savedContact = MyPreferences.getString(getContext(), "user_phone_number", "0326749576");
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

        String[] addressParts = tvAddress.getText().toString().replace("Address: ", "").split(",");
        if (addressParts.length >= 3) {
            etCountry.setText(addressParts[0].trim());
            etCity.setText(addressParts[1].trim());
            etDetailedAddress.setText(addressParts[2].trim());
        }
        etContact.setText(tvContact.getText().toString().replace("Contact: ", "").trim());

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

            MyPreferences.setString(getContext(), "user_address", fullAddress);
            MyPreferences.setString(getContext(), "user_phone_number", fullContact);

            tvAddress.setText("Address: " + fullAddress);
            tvContact.setText("Contact: " + fullContact);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateSelectAllState() {
        boolean allSelected = !cartItems.isEmpty();
        for (Product item : cartItems) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        cbSelectAll.setChecked(allSelected);
    }

    private void updateTotalAmount() {
        double total = 0.0;
        for (Product item : cartItems) {
            if (item.isSelected() && item.getQuantity() != null) {
                double price = item.getPrice();
                if (item.getDiscounts() != null && !item.getDiscounts().isEmpty()) {
                    Discount discount = selectBestDiscount(item.getDiscounts(), price * item.getQuantity());
                    if (discount != null) {
                        if ("percent".equals(discount.getType())) {
                            price *= (1 - Double.parseDouble(discount.getValue()) / 100);
                        } else {
                            price -= Double.parseDouble(discount.getValue()) / item.getQuantity();
                        }
                    }
                }
                total += price * item.getQuantity();
            }
        }
        tvTotalAmount.setText("đ" + String.format("%.2f", total));
    }

    private Discount selectBestDiscount(List<Discount> discounts, double orderValue) {
        Discount bestDiscount = null;
        double maxDiscount = 0;
        for (Discount discount : discounts) {
            try {
                double minOrder = Double.parseDouble(discount.getMinValueOrders());
                if (orderValue >= minOrder) {
                    double discountValue;
                    if ("percent".equals(discount.getType())) {
                        discountValue = orderValue * Double.parseDouble(discount.getValue()) / 100;
                    } else {
                        discountValue = Double.parseDouble(discount.getValue());
                    }
                    if (discountValue > maxDiscount) {
                        maxDiscount = discountValue;
                        bestDiscount = discount;
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        return bestDiscount;
    }

    private void saveCart() {
        CartProducts cartProducts = new CartProducts(cartItems);
        MyPreferences.setString(getContext(), "cart_products", cartProducts.toJson());
    }
}