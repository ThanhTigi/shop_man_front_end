package com.example.shopman.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopman.R;

public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView imageView = findViewById(R.id.fullScreenImage);
        String imageUri = getIntent().getStringExtra("image_uri");
        float scaleFactor = getIntent().getFloatExtra("scale_factor", 1.0f);

        if (imageUri != null) {
            Glide.with(this)
                    .load(imageUri)
                    .apply(new RequestOptions().override((int) (imageView.getWidth() * scaleFactor), (int) (imageView.getHeight() * scaleFactor)))
                    .into(imageView);
        }
    }
}