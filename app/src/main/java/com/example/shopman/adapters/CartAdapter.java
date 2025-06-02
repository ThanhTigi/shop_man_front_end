package com.example.shopman.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final Context context;
    private final List<Product> cartItems;
    private OnCartItemChangeListener listener;

    public CartAdapter(Context context, List<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartItems.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice, tvQuantity;
        CheckBox cartCheckbox;
        Button btnDecrease, btnIncrease, btnRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            cartCheckbox = itemView.findViewById(R.id.cartCheckbox);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            cartCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isShown() && listener != null) {
                    listener.onItemSelectionChanged(cartItems.get(getAdapterPosition()), isChecked);
                }
            });

            btnDecrease.setOnClickListener(v -> {
                Product product = cartItems.get(getAdapterPosition());
                int quantity = product.getQuantity() != null ? product.getQuantity() : 1;
                if (quantity > 1 && listener != null) {
                    listener.onQuantityChanged(product, quantity - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                Product product = cartItems.get(getAdapterPosition());
                int quantity = product.getQuantity() != null ? product.getQuantity() : 1;
                if (listener != null) {
                    listener.onQuantityChanged(product, quantity + 1);
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(cartItems.get(getAdapterPosition()));
                }
            });
        }

        void bind(Product product) {
            Glide.with(context)
                    .load(product.getThumb())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(productImage);

            productName.setText(product.getName());

            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            productPrice.setText(String.format("đ%s", formatter.format(product.getPrice())));

            cartCheckbox.setChecked(product.isSelected());

            tvQuantity.setText(String.valueOf(product.getQuantity() != null ? product.getQuantity() : 1));
        }
    }

    public interface OnCartItemChangeListener {
        void onItemSelectionChanged(Product product, boolean isChecked);
        void onQuantityChanged(Product product, int quantity);
        void onRemoveItem(Product product);
    }
}