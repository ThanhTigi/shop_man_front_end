package com.example.shopman.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.models.CampaignResponse;
import com.example.shopman.models.ShopResponse;

import java.util.List;

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.DiscountViewHolder> {
    private final List<Object> discounts; // Hỗ trợ cả CampaignResponse.Discount và ShopResponse.Discount

    public DiscountAdapter(List<Object> discounts) {
        this.discounts = discounts;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Object discountObj = discounts.get(position);
        String name, code, value, type;

        if (discountObj instanceof CampaignResponse.Discount) {
            CampaignResponse.Discount discount = (CampaignResponse.Discount) discountObj;
            name = discount.getName();
            code = discount.getCode();
            value = discount.getValue();
            type = discount.getType();
        } else if (discountObj instanceof ShopResponse.Discount) {
            ShopResponse.Discount discount = (ShopResponse.Discount) discountObj;
            name = discount.getName();
            code = discount.getCode();
            value = discount.getValue();
            type = discount.getType();
        } else {
            return;
        }

        holder.discountName.setText(name);
        holder.discountCode.setText("Mã: " + code);
        String discountText = type.equals("percent") ? value + "%" : value + "đ";
        holder.discountValue.setText("Giảm: " + discountText);
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    static class DiscountViewHolder extends RecyclerView.ViewHolder {
        TextView discountName, discountCode, discountValue;

        DiscountViewHolder(View itemView) {
            super(itemView);
            discountName = itemView.findViewById(R.id.discountName);
            discountCode = itemView.findViewById(R.id.discountCode);
            discountValue = itemView.findViewById(R.id.discountValue);
        }
    }
}