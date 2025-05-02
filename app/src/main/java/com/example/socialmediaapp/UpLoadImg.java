package com.example.socialmediaapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UpLoadImg {
    private static final String TAG = "UpLoadImg";
    private Context context;
    private CloudinaryConfig cloudinaryConfig;

    public UpLoadImg(Context context) {
        this.context = context;
        cloudinaryConfig = new CloudinaryConfig();
    }

    // Upload ảnh lên Cloudinary
    public void uploadImg(Uri imageUri, final UploadListener listener) {
        // Log the image URI being uploaded
        Log.d(TAG, "Uploading image: " + imageUri.toString());

        cloudinaryConfig.uploadImage(imageUri, new CloudinaryConfig.UploadCallback() {
            @Override
            public void onUploadSuccess(String imageUrl) {
                // Log the success and the uploaded image URL
                Log.d(TAG, "Upload successful! Image URL: " + imageUrl);
                listener.onUploaded(imageUrl);
            }

            @Override
            public void onUploadFailed(String error) {
                // Log the failure with the error message
                Log.e(TAG, "Upload failed: " + error);
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
