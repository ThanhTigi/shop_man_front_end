package com.example.shopman.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopman.R;
import com.example.shopman.activities.ProductDetailsActivity;
import com.example.shopman.models.Product;
import com.example.shopman.models.searchproducts.SearchProduct;

import java.util.List;
import java.util.Locale;

public class DealProductAdapter extends RecyclerView.Adapter<DealProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<SearchProduct> productList;

    public DealProductAdapter(Context context, List<SearchProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_single_horizontal, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (position < productList.size()) {
            SearchProduct product = productList.get(position);
            holder.productName.setText(product.getName());
            holder.productDescription.setText(product.getDesc() != null ? product.getDesc() : "");
            try {
                double priceValue = Double.parseDouble(product.getPrice());
                holder.productPrice.setText(String.format(Locale.getDefault(), "đ%.0f", priceValue));
            } catch (NumberFormatException e) {
                holder.productPrice.setText(product.getPrice() != null ? product.getPrice() : "N/A");
            }
            if (product.getDiscountPercentage() > 0) {
                holder.productDiscount.setVisibility(View.VISIBLE);
                holder.productDiscount.setText(String.format(Locale.getDefault(), "-%d%%", product.getDiscountPercentage()));
            } else {
                holder.productDiscount.setVisibility(View.GONE);
            }
            holder.productSaleCount.setText(String.format(Locale.getDefault(), "Sold: %d", product.getSaleCount()));
            holder.productRating.setRating(product.getRating());

            Glide.with(context)
                    .load(product.getThumb())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error))
                    .into(holder.productImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("product_slug", product.getSlug());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(productList.size(), 10); // Giới hạn tối đa 10 sản phẩm
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productDiscount, productSaleCount;
        RatingBar productRating;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDiscount = itemView.findViewById(R.id.productDiscount);
            productSaleCount = itemView.findViewById(R.id.productSaleCount);
            productRating = itemView.findViewById(R.id.productRating);
        }
    }
}