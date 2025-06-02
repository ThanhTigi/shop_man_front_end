package com.example.shopman.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopman.R;
import com.example.shopman.models.Banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Banner> bannerList;
    private OnBannerClickListener onBannerClickListener;

    // Interface để xử lý sự kiện nhấp vào banner
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
        Banner banner = bannerList.get(position % bannerList.size()); // Vòng lặp vô hạn cho ViewPager2
        // Sử dụng Picasso để tải hình ảnh banner
//        if (banner.getThumb() != null && !banner.getThumb().isEmpty()) {
//            Picasso.get()
//                    .load(banner.getThumb())
//                    .placeholder(R.drawable.ic_placeholder)
//                    .error(R.drawable.ic_error)
//                    .into(holder.bannerImage);
//        } else {
//            holder.bannerImage.setImageResource(R.drawable.ic_placeholder);
//        }

        // Xử lý sự kiện nhấp vào banner
        holder.itemView.setOnClickListener(v -> {
            if (onBannerClickListener != null) {
                onBannerClickListener.onBannerClicked(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.isEmpty() ? 0 : Integer.MAX_VALUE; // Vòng lặp vô hạn cho ViewPager2
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