package com.example.shopman.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.shopman.R;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final Context context;
    private final List<String> imageUrls = new ArrayList<>();

    public MediaAdapter(Context context) {
        this.context = context;
    }

    public void setMediaItems(List<String> urls) {
        imageUrls.clear();
        imageUrls.addAll(urls);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(context).load(url).into(holder.ivMedia);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivMedia;

        MediaViewHolder(View itemView) {
            super(itemView);
            ivMedia = itemView.findViewById(R.id.ivMedia);
        }
    }
}