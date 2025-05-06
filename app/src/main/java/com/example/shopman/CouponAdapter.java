package com.example.shopman;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Coupon> coupons;
    private OnCouponSelectedListener listener;

    public interface OnCouponSelectedListener {
        void onCouponSelected(Coupon coupon);
    }

    public CouponAdapter(List<Coupon> coupons, OnCouponSelectedListener listener) {
        this.coupons = coupons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.tvCouponCode.setText("Code: " + coupon.getCode());
        holder.tvDiscountPercentage.setText("Discount: " + coupon.getDiscountPercentage() + "%");
        holder.tvExpiryDate.setText("Expiry: " + coupon.getExpiryDate());
        holder.tvRemainingQuantity.setText("Remaining: " + coupon.getRemainingQuantity());
        holder.itemView.setOnClickListener(v -> listener.onCouponSelected(coupon));
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponCode, tvDiscountPercentage, tvExpiryDate, tvRemainingQuantity;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvDiscountPercentage = itemView.findViewById(R.id.tvDiscountPercentage);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvRemainingQuantity = itemView.findViewById(R.id.tvRemainingQuantity);
        }
    }
}