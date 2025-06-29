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

import com.example.shopman.R;
import com.example.shopman.activities.ProductDetailsActivity;
import com.example.shopman.models.searchproducts.SearchProduct;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DealVerticalProductAdapter extends RecyclerView.Adapter<DealVerticalProductAdapter.ViewHolder> {

    private Context context;
    private List<SearchProduct> productList;

    public DealVerticalProductAdapter(Context context, List<SearchProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deal_product_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchProduct product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDesc());
        holder.productPrice.setText("đ" + product.getPrice());
        holder.productRating.setRating(product.getRating());
        holder.productSaleCount.setText("Sold: " + product.getSaleCount());

        Picasso.get().load(product.getThumb()).into(holder.productImage);

        // Thêm xử lý click để truyền slug qua Intent
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_slug", product.getSlug());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productSaleCount;
        RatingBar productRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            productSaleCount = itemView.findViewById(R.id.productSaleCount);
        }
    }
}