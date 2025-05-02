package com.example.socialmediaapp;

import android.os.AsyncTask;
import android.net.Uri;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

public class CloudinaryConfig {

    private static final String CLOUDINARY_URL = "cloudinary://938645584612123:8pgVUwyep5CnZjALe2iz-G-fRbU@dyovcjrb9";
    private Cloudinary cloudinary;

    public CloudinaryConfig() {
        cloudinary = new Cloudinary(CLOUDINARY_URL);
    }

    // Hàm upload ảnh lên Cloudinary (sử dụng AsyncTask)
    public void uploadImage(final Uri imageUri, final UploadCallback callback) {
        new UploadImageTask(callback).execute(imageUri);
    }

    // AsyncTask để tải ảnh lên Cloudinary trong background thread
    private class UploadImageTask extends AsyncTask<Uri, Void, String> {

        private UploadCallback callback;

        public UploadImageTask(UploadCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Uri... uris) {
            try {
                Uri imageUri = uris[0];
                InputStream inputStream = MyApplication.getAppContext().getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                    return (String) uploadResult.get("url");
                } else {
                    return "Failed to open InputStream for the image URI.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String imageUrl) {
            // Ensure the image URL is HTTPS
            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            if (imageUrl.startsWith("https://")) {
                callback.onUploadSuccess(imageUrl);  // Call the callback on success
            } else {
                callback.onUploadFailed(imageUrl);  // Call the callback on failure
            }
        }

    }

    // Callback để xử lý kết quả upload ảnh
    public interface UploadCallback {
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(String error);
    }
}