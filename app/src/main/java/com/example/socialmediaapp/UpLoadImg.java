package com.example.socialmediaapp;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UpLoadImg {
    private Context context;
    private CloudinaryConfig cloudinaryConfig;

    public UpLoadImg(Context context) {
        this.context = context;
        cloudinaryConfig = new CloudinaryConfig();
    }

    // Upload ảnh lên Cloudinary
    public void uploadImg(Uri imageUri, final UploadListener listener) {
        cloudinaryConfig.uploadImage(imageUri, new CloudinaryConfig.UploadCallback() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                Toast.makeText(context, "Upload thành công!", Toast.LENGTH_SHORT).show();
                listener.onUploaded(imageUrl);
            }

            @Override
            public void onUploadFailed(String error) {
                Toast.makeText(context, "Upload thất bại: " + error, Toast.LENGTH_SHORT).show();
                listener.onFailed(error);
            }
        });
    }

    // Gán ảnh từ URL vào ImageView
    public void setImageToView(String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    // Interface callback để xử lý sau khi upload
    public interface UploadListener {
        void onUploaded(String imageUrl);
        void onFailed(String error);
    }
}
