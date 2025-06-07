package com.example.shopman.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.models.Discount;
import com.example.shopman.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private final List<Product> products;
    private final String displayType; // "search", "cart", "trending", "category", etc.
    private OnProductClickListener listener;

    public ProductAdapter(Context context, List<Product> products, String displayType) {
        this.context = context;
        this.products = products;
        this.displayType = displayType;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> moreProducts) {
        int startPosition = products.size();
        products.addAll(moreProducts);
        notifyItemRangeInserted(startPosition, moreProducts.size());
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productDescription, productPrice, productDiscount, productSaleCount;
        RatingBar productRating;
        CardView cardView;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDiscount = itemView.findViewById(R.id.productDiscount);
            productSaleCount = itemView.findViewById(R.id.productSaleCount);
            productRating = itemView.findViewById(R.id.productRating);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(products.get(getAdapterPosition()));
                }
            });
        }

        void bind(Product product) {
            // Hình ảnh sản phẩm
            Glide.with(context)
                    .load(product.getThumb())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(productImage);

            // Tên sản phẩm
            productName.setText(product.getName());

            // Mô tả sản phẩm
            if ("cart".equals(displayType) && product.getQuantity() != null) {
                productDescription.setText(String.format("Qty: %d", product.getQuantity()));
                productDescription.setVisibility(View.VISIBLE);
            } else if (product.getDesc() != null && !product.getDesc().isEmpty()) {
                productDescription.setText(product.getDesc());
                productDescription.setVisibility(View.VISIBLE);
            } else {
                productDescription.setVisibility(View.GONE);
            }

            // Giá sản phẩm
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            productPrice.setText(String.format("đ%s", formatter.format(product.getPrice())));

            // Giảm giá
            if (product.getDiscountPercentage() > 0) {
                productDiscount.setVisibility(View.VISIBLE);
                productDiscount.setText(String.format("%d%% OFF", product.getDiscountPercentage()));
            } else if (product.getDiscounts() != null && !product.getDiscounts().isEmpty()) {
                Discount discount = product.getDiscounts().get(0);
                productDiscount.setVisibility(View.VISIBLE);
                productDiscount.setText(String.format("%s%s OFF",
                        discount.getValue(),
                        "percent".equals(discount.getType()) ? "%" : ""));
            } else {
                productDiscount.setVisibility(View.GONE);
            }

            // Đánh giá
            float rating = product.getRating();
            if (rating >= 0 && rating <= 5) {
                productRating.setRating(rating);
                productRating.setVisibility("cart".equals(displayType) ? View.GONE : View.VISIBLE);
            } else {
                productRating.setVisibility(View.GONE);
            }

            // Số lượng bán
            productSaleCount.setText(String.format("Sold: %d", product.getSaleCount()));
            productSaleCount.setVisibility("cart".equals(displayType) ? View.GONE : View.VISIBLE);
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
}