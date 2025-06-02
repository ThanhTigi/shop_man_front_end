package com.example.shopman.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.models.cart.CartItemResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private final List<CartItemResponse> cartItems;

    public CheckoutAdapter(List<CartItemResponse> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItemResponse cartItem = cartItems.get(position);

        // Lấy thông tin từ CartItemResponse
        String productName = cartItem.getProductName();
        String imageUrl = cartItem.getImage();
        int quantity = cartItem.getQuantity();
        String price = cartItem.getPrice();

        // Hiển thị hình ảnh sản phẩm bằng Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder) // Hình ảnh placeholder
                    .error(R.drawable.ic_error) // Hình ảnh khi lỗi
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_placeholder);
        }

        // Hiển thị tên sản phẩm
        holder.productName.setText(productName != null ? productName : "Không xác định");

        // Hiển thị mô tả (giả sử dùng productName làm mô tả tạm thời vì không có desc)
        holder.productDescription.setText(productName != null ? productName : "Không có mô tả");

        // Hiển thị kích thước từ variant
        String size = "N/A";
        Map<String, Object> variant = cartItem.getVariant();
        if (variant != null && variant.containsKey("size")) {
            size = String.valueOf(variant.get("size"));
        }
        holder.productSize.setText("Size: " + size);

        // Hiển thị số lượng
        holder.productQuantity.setText("Số lượng: " + quantity);

        // Tính ngày giao hàng (ví dụ: 7 ngày kể từ hôm nay)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String deliveryDate = "Giao hàng dự kiến: " + sdf.format(calendar.getTime());
        holder.deliveryDate.setText(deliveryDate);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productSize, productQuantity, deliveryDate;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productSize = itemView.findViewById(R.id.productSize);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            deliveryDate = itemView.findViewById(R.id.deliveryDate);
        }
    }
}