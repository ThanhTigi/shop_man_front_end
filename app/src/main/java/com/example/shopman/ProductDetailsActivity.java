package com.example.shopman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.shopman.models.ProductDetails.ProductDetail;
import com.example.shopman.models.ProductDetails.ProductDetailResponse;
import com.example.shopman.models.Sku;
import com.example.shopman.models.SpuToSku;

import com.example.shopman.remote.ApiManager;
import com.example.shopman.remote.ApiResponseListener;

import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvName, tvPrice, tvDescription, tvRating;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ivProduct = findViewById(R.id.ivProduct);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvRating = findViewById(R.id.tvRating);

        apiManager = new ApiManager(this);

        String slug = getIntent().getStringExtra("product_slug");
        if (slug != null) {
            loadProductDetail(slug);
        } else {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductDetail(String slug) {
        apiManager.getProductDetail(slug, new ApiResponseListener<ProductDetailResponse>() {
            @Override
            public void onSuccess(ProductDetailResponse response) {
                if (response.getMetadata() != null && response.getMetadata().getMetadata() != null) {
                    ProductDetail productDetail = response.getMetadata().getMetadata();
                    Product product = mapProductDetailToProduct(productDetail);
                    displayProduct(product);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Invalid response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(ProductDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Product mapProductDetailToProduct(ProductDetail pd) {
        Product product = new Product();
        product.setId(String.valueOf(pd.getId()));
        product.setName(pd.getName());
        product.setDesc(pd.getDesc());
        product.setDesc_plain(pd.getDesc_plain());
        product.setPrice(pd.getPrice());
        product.setThumb(pd.getThumb());
        product.setRating(pd.getRating());
        product.setDiscount_percentage(pd.getDiscount_percentage());
        product.setSlug(pd.getSlug());
        product.setCategoryId(pd.getCategoryId());
        product.setCategoryPath(pd.getCategoryPath());
        product.setShopId(pd.getShopId());
        product.setSale_count(pd.getSale_count());
        product.setAttrs(pd.getAttrs());
        product.setSpuToSkus(pd.getSpuToSkus());
        product.setHas_variations(pd.isHas_variations());
        return product;
    }

    private void displayProduct(Product product) {
        tvName.setText(product.getName());
        tvPrice.setText(product.getPrice());
        tvDescription.setText(product.getDesc());
        tvRating.setText(String.valueOf(product.getRating()));
        Glide.with(this).load(product.getThumb()).into(ivProduct);

        // Hiển thị attrs
        Map<String, Object> attrs = product.getAttrs();
        if (attrs != null) {
            List<String> sizes = (List<String>) attrs.get("sizes");
            // Hiển thị sizes, colors, v.v. trong UI
        }

        // Hiển thị SpuToSkus
        List<SpuToSku> skus = product.getSpuToSkus();
        if (skus != null) {
            for (SpuToSku spuToSku : skus) {
                Sku sku = spuToSku.getSku();
                Map<String, String> skuAttrs = sku.getSkuAttr().getSku_attrs();
                // Hiển thị size, color, stock trong UI
            }
        }
    }
}