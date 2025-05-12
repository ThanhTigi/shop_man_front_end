package com.example.shopman.fragments.cart;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.Product;
import com.example.shopman.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onItemSelectionChanged();
        void onQuantityChanged();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.cbSelect.setChecked(cartItem.isSelected());
        holder.productImage.setImageResource(product.getImageResId());
        holder.productName.setText(product.getName());
        holder.productVariations.setText("Variations: " + cartItem.getSelectedVariation());
        holder.productRating.setRating(product.getRating());
        holder.ratingText.setText(String.valueOf(product.getRating()));
        holder.productPrice.setText(product.getPrice());

        String priceStr = product.getPrice().replace("$", "").trim();
        double price = Double.parseDouble(priceStr);
        double originalPrice = price / 0.67;
        holder.productOriginalPrice.setText("$" + String.format("%.2f", originalPrice));
        holder.productOriginalPrice.setPaintFlags(holder.productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.productDiscount.setText("upto 33% off");
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvTotalOrder.setText("TOTAL ORDER (" + cartItem.getQuantity() + "): $" + String.format("%.2f", cartItem.getTotalPrice()));

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cartItem.setSelected(isChecked);
            listener.onItemSelectionChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            if (quantity > 1) {
                cartItem.setQuantity(quantity - 1);
                holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                holder.tvTotalOrder.setText("TOTAL ORDER (" + cartItem.getQuantity() + "): $" + String.format("%.2f", cartItem.getTotalPrice()));
                listener.onQuantityChanged();
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity + 1);
            holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            holder.tvTotalOrder.setText("TOTAL ORDER (" + cartItem.getQuantity() + "): $" + String.format("%.2f", cartItem.getTotalPrice()));
            listener.onQuantityChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView productImage;
        TextView productName, productVariations, ratingText, productPrice, productOriginalPrice, productDiscount, tvQuantity, tvTotalOrder;
        RatingBar productRating;
        TextView btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productVariations = itemView.findViewById(R.id.productVariations);
            productRating = itemView.findViewById(R.id.productRating);
            ratingText = itemView.findViewById(R.id.ratingText);
            productPrice = itemView.findViewById(R.id.productPrice);
            productOriginalPrice = itemView.findViewById(R.id.productOriginalPrice);
            productDiscount = itemView.findViewById(R.id.productDiscount);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalOrder = itemView.findViewById(R.id.tvTotalOrder);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}