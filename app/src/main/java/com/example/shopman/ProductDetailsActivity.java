package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.shopman.fragments.cart.CartItem;
import com.example.shopman.fragments.cart.CartProducts;
import com.example.shopman.fragments.cart.CheckoutActivity;
import com.example.shopman.fragments.wishlist.WishlistProducts;
import com.example.shopman.utilitis.MyPreferences;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice, productDetailedDescription;
    private RatingBar productRating;
    private TextView selectedSizeText;
    private LinearLayout sizeContainer;
    private Button goToCartButton, buyNowButton;
    private Button selectedSizeButton;
    private ImageView backImageView;

    private Product product;
    private WishlistProducts wishlistProducts;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        productDetailedDescription = findViewById(R.id.productDetailedDescription);
        productRating = findViewById(R.id.productRating);
        selectedSizeText = findViewById(R.id.selectedSizeText);
        sizeContainer = findViewById(R.id.sizeContainer);
        goToCartButton = findViewById(R.id.goToCartButton);
        buyNowButton = findViewById(R.id.buyNowButton);
        backImageView = findViewById(R.id.backiv);


        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            productImage.setImageResource(product.getImageResId());
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(product.getPrice());
            productDetailedDescription.setText(product.getDetailedDescription());
            productRating.setRating(product.getRating());

            List<String> sizes = product.getSizes();
            for (int i = 0; i < sizes.size(); i++) {
                String size = sizes.get(i);
                Button sizeButton = new Button(this);
                sizeButton.setText(size);
                sizeButton.setTextSize(14);
                sizeButton.setBackgroundResource(R.drawable.size_button_background);
                sizeButton.setTextColor(getResources().getColor(android.R.color.black));
                sizeButton.setPadding(16, 8, 16, 8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(8, 0, 8, 0);
                sizeButton.setLayoutParams(params);

                if (i == 0) {
                    sizeButton.setSelected(true);
                    selectedSizeButton = sizeButton;
                    selectedSizeText.setText("Type: " + size);
                }

                sizeButton.setOnClickListener(v -> {
                    if (selectedSizeButton != null) {
                        selectedSizeButton.setSelected(false);
                    }
                    sizeButton.setSelected(true);
                    selectedSizeButton = sizeButton;
                    selectedSizeText.setText("Type: " + size);
                });

                sizeContainer.addView(sizeButton);
            }
        } else {
            Toast.makeText(this, "Product data not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        String jsonData = MyPreferences.getString(this,"user_wishlist_products","");
        wishlistProducts = new WishlistProducts(new ArrayList<>());
        if (!jsonData.isEmpty())
        {
            wishlistProducts.setTotalProducts(WishlistProducts.fromJson(jsonData).getTotalProducts());
        }


        AppCompatButton heartButton = findViewById(R.id.heartButton);

        boolean checkContains = false;
        for (Product product1: wishlistProducts.getTotalProducts()) {
            if (product1.getName().equals(product.getName()))
            {
                checkContains = true;
                break;
            }
        }

        heartButton.setSelected(checkContains);

        heartButton.setOnClickListener(v -> {
            boolean nowSelected = !heartButton.isSelected();
            heartButton.setSelected(nowSelected);
            if (nowSelected)
            {
                wishlistProducts.AddProduct(product);
                MyPreferences.setString(this,"user_wishlist_products",wishlistProducts.toJson());
            }
            else
            {
                wishlistProducts.RemoveProduct(product);
                MyPreferences.setString(this,"user_wishlist_products",wishlistProducts.toJson());
            }
        });

        goToCartButton.setOnClickListener(v ->
        {
            CartProducts currentCartProducts;
            if (MyPreferences.getString(this,"cart_products","").isEmpty())
            {
                currentCartProducts = new CartProducts(new ArrayList<>());
            }
            else
            {
                currentCartProducts = CartProducts.fromJson(MyPreferences.getString(this,"cart_products",""));
            }


            boolean check = false;
            for (CartItem cartItem:currentCartProducts.getProducts()) {
                if (cartItem.getProduct().getName().equals(product.getName()))
                {
                    check = true;
                    break;
                }
            }
            if (!check)
            {
                currentCartProducts.getProducts().add(new CartItem(product,1,
                        false,selectedSizeButton.getText().toString()));

                MyPreferences.setString(this,"cart_products",currentCartProducts.toJson());
            }

            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });
        buyNowButton.setOnClickListener(v->
        {
            CartItem item = new CartItem(product,1,true,selectedSizeButton.getText().toString());
            ArrayList<CartItem> selectedItems = new ArrayList<>();
            selectedItems.add(item);
            Intent checkoutIntent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
            checkoutIntent.putExtra("selectedCartItems", selectedItems);
            startActivity(checkoutIntent);
        });

    }
}