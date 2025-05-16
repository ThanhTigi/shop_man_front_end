package com.example.shopman;

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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvDescription.setText(product.getDesc()); // Thêm desc
        holder.tvPrice.setText("đ" + product.getPrice());
        holder.ratingBar.setRating(product.getRating());
        Glide.with(holder.itemView.getContext()).load(product.getThumb()).into(holder.ivProduct);

        // Hiển thị discount_percentage
        if (product.getDiscount_percentage() > 0) {
            holder.tvDiscount.setText(product.getDiscount_percentage() + "% OFF");
            holder.tvDiscount.setVisibility(View.VISIBLE);
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }

        // Hiển thị sale_count
        holder.tvSaleCount.setText("Sold: " + product.getSale_count());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvDescription, tvPrice, tvDiscount, tvSaleCount;
        RatingBar ratingBar;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.productImage);
            tvName = itemView.findViewById(R.id.productName);
            tvDescription = itemView.findViewById(R.id.productDescription);
            tvPrice = itemView.findViewById(R.id.productPrice);
            tvDiscount = itemView.findViewById(R.id.productDiscount); // Sửa từ findById thành findViewById
            tvSaleCount = itemView.findViewById(R.id.productSaleCount);
            ratingBar = itemView.findViewById(R.id.productRating);
        }
    }
}