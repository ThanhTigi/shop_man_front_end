package com.example.shopman.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.models.Discount;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {
    private final Context context;
    private final List<Discount> discounts;
    private OnDiscountClickListener listener;

    public DiscountAdapter(Context context, List<Discount> discounts) {
        this.context = context;
        this.discounts = discounts;
    }

    public void setOnDiscountClickListener(OnDiscountClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Discount discount = discounts.get(position);
        holder.bind(discount);
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiscountName, tvDiscountValue;

        DiscountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiscountName = itemView.findViewById(R.id.tvDiscountName);
            tvDiscountValue = itemView.findViewById(R.id.tvDiscountValue);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDiscountClick(discounts.get(getAdapterPosition()));
                }
            });
        }

        void bind(Discount discount) {
            tvDiscountName.setText(discount.getName());
            tvDiscountValue.setText(discount.getValue() + (discount.getType().equals("percent") ? "%" : "đ"));
        }
    }

    public interface OnDiscountClickListener {
        void onDiscountClick(Discount discount);
    }
}