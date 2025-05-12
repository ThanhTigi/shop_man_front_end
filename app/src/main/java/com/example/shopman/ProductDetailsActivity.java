package com.example.shopman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        Product product = (Product) getIntent().getSerializableExtra("product");
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

        goToCartButton.setOnClickListener(v -> Toast.makeText(this, "Go to Cart clicked", Toast.LENGTH_SHORT).show());
        buyNowButton.setOnClickListener(v -> Toast.makeText(this, "Buy Now clicked", Toast.LENGTH_SHORT).show());

    }
}