package com.example.shopman.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopman.R;
import com.example.shopman.activities.FullScreenImageActivity;

import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private static final String TAG = "MediaAdapter";
    private final Context context;
    private final List<String> imageUrls = new ArrayList<>();

    public MediaAdapter(Context context) {
        this.context = context;
    }

    public void setMediaItems(List<String> urls) {
        imageUrls.clear();
        if (urls != null) {
            imageUrls.addAll(urls);
        } else {
            Log.w(TAG, "setMediaItems: urls is null");
        }
        Log.d(TAG, "setMediaItems: imageUrls size = " + imageUrls.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_media, parent, false);
        Log.d(TAG, "onCreateViewHolder called");
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Log.d(TAG, "onBindViewHolder: position = " + position + ", url = " + url);
        Glide.with(context).load(url).into(holder.ivMedia);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Image clicked: " + url);
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putStringArrayListExtra("image_urls", new ArrayList<>(imageUrls)); // Truyền toàn bộ danh sách
            intent.putExtra("initial_position", position); // Vị trí ảnh được nhấn
            intent.putExtra("scale_factor", 1.5f);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        int size = imageUrls.size();
        Log.d(TAG, "getItemCount: " + size);
        return size;
    }

    static class MediaViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivMedia;

        MediaViewHolder(View itemView) {
            super(itemView);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            Log.d("MediaViewHolder", "Initialized with ivMedia");
        }
    }
}