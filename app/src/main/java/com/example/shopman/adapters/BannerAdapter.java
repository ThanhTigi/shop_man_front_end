package com.example.shopman.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.models.Banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> bannerList;
    private OnBannerClickListener onBannerClickListener;

    public interface OnBannerClickListener {
        void onBannerClicked(Banner banner);
    }

    public BannerAdapter(List<Banner> bannerList, OnBannerClickListener listener) {
        this.bannerList = bannerList != null ? bannerList : new ArrayList<>();
        this.onBannerClickListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (bannerList.isEmpty()) return;
        Banner banner = bannerList.get(position % bannerList.size());

        // Tải hình ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(banner.getThumb())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .centerCrop()
                .into(holder.bannerImage);

        // Xử lý click
        holder.itemView.setOnClickListener(v -> {
            if (onBannerClickListener != null) {
                onBannerClickListener.onBannerClicked(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    public void updateBanners(List<Banner> newBanners) {
        this.bannerList.clear();
        if (newBanners != null) {
            this.bannerList.addAll(newBanners);
        }
        notifyDataSetChanged();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }
}