package com.example.socialmediaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragment_home extends Fragment {
    private static final String TAG = "HomeFragment";
    private int taiKhoanId;
    private ImageView imgAvatar_Home, imgPicture_Home;
    private Button btnPost_Home;
    private EditText txtContent_Home;
    private ListView lvPost_Home;
    private MultiTypeAdapter mulAdapter;
    private UpLoadImg upLoadImg;
    private String uploadedImageUrl = null;
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageButton btnPicture_Home, btnResetContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        imgAvatar_Home = view.findViewById(R.id.imgAvatar_Home);
        imgPicture_Home = view.findViewById(R.id.imgPicture_Home);
        btnPost_Home = view.findViewById(R.id.btnPost_Home);
        btnPicture_Home = view.findViewById(R.id.btnPicture_Home);
        txtContent_Home = view.findViewById(R.id.txtContent_Home);
        lvPost_Home = view.findViewById(R.id.lvPost_Home);
        btnResetContent = view.findViewById(R.id.btnResetContent);

        upLoadImg = new UpLoadImg(getContext());

        // Check login status
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);
        if (taiKhoanId == -1) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
            return view;
        }

        // Set avatar image
        String url_anh_dai_dien = prefs.getString("url_anh_dai_dien", "");
        if(TextUtils.isEmpty(url_anh_dai_dien))
            imgAvatar_Home.setImageResource(R.drawable.default_profile_image);
        else
            upLoadImg.setImageToView(url_anh_dai_dien, imgAvatar_Home);

        // Post button click listener
        btnPost_Home.setOnClickListener(v -> {
            String content = txtContent_Home.getText().toString();
            if (content.isEmpty())
                Toast.makeText(getContext(), "Please enter content", Toast.LENGTH_SHORT).show();
            else
                postBaiDang(taiKhoanId);
        });

        // Image picker launch logic
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();

                            // Log selected image URI
                            Log.d(TAG, "Image URI selected: " + selectedImageUri.toString());

                            imgPicture_Home.setImageURI(selectedImageUri);

                            // Upload selected image
                            if (selectedImageUri != null) {
                                Log.d(TAG, "Image picked successfully.");
                                upLoadImg.uploadImg(selectedImageUri, new UpLoadImg.UploadListener() {
                                    @Override
                                    public void onUploaded(String imageUrl) {
                                        uploadedImageUrl = imageUrl;
                                        Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                        upLoadImg.setImageToView(uploadedImageUrl, imgPicture_Home);
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Toast.makeText(getContext(), "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "No image data received.");
                            if (imgPicture_Home.getVisibility() != View.GONE) {
                                if (selectedImageUri == null) {
                                    resetImagePicker();
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, "Image picking cancelled or failed.");
                        if (imgPicture_Home.getVisibility() != View.GONE) {
                            if (selectedImageUri == null) {
                                resetImagePicker();
                            }
                        }
                    }
                }
        );

        // Button for selecting an image
        btnPicture_Home.setOnClickListener(v -> {
            imgPicture_Home.setVisibility(View.VISIBLE);
            // Launch image picker
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);

            LinearLayout.LayoutParams content_HomeParams = (LinearLayout.LayoutParams) txtContent_Home.getLayoutParams();
            content_HomeParams.weight = 3;
            txtContent_Home.setLayoutParams(content_HomeParams);

            LinearLayout.LayoutParams imgPicture_HomeParams = (LinearLayout.LayoutParams) imgPicture_Home.getLayoutParams();
            imgPicture_HomeParams.weight = 1;
            imgPicture_Home.setLayoutParams(imgPicture_HomeParams);

        });

        // Reset button functionality
        btnResetContent.setOnClickListener(v -> {
            txtContent_Home.setText("");
            resetImagePicker();
        });

        // Initialize adapter and load posts
        mulAdapter = new MultiTypeAdapter(this.getContext(), new ArrayList<>(), "Post");
        lvPost_Home.setAdapter(mulAdapter);

        loadPosts(taiKhoanId);

        return view;
    }

    // Reset image picker
    private void resetImagePicker() {
        imgPicture_Home.setImageURI(null);
        imgPicture_Home.setImageDrawable(null);
        selectedImageUri = null;
        uploadedImageUrl = null;
        imgPicture_Home.setVisibility(View.GONE);
        LinearLayout.LayoutParams content_HomeParams = (LinearLayout.LayoutParams) txtContent_Home.getLayoutParams();
        content_HomeParams.weight = 4;
        txtContent_Home.setLayoutParams(content_HomeParams);
    }

    // Post content with or without image
    private void postBaiDang(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("noidung", txtContent_Home.getText().toString());
                if (uploadedImageUrl != null)
                    requestData.put("url", uploadedImageUrl);

                JSONObject response = ApiClient.post("new_post_url.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(this.getContext(), "Post successfully", Toast.LENGTH_SHORT).show();
                        txtContent_Home.setText(""); // Clear input
                        resetImagePicker(); // Reset image picker
                        loadPosts(taiKhoanId); // Reload posts
                        imgPicture_Home.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this.getContext(), "Failed to upload new post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(this.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error posting", e);
                });
            } finally {
                executor.shutdown();
            }
        });
    }

    // Load posts for the user
    private void loadPosts(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<PostItem> lstPostItem = new ArrayList<PostItem>();
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);

                JSONObject response = ApiClient.post("get_Post.php", requestData);

                if (response != null && response.optBoolean("success", false)) {
                    for (int i = 0; i < response.getJSONArray("posts").length(); i++) {
                        JSONObject post = response.getJSONArray("posts").getJSONObject(i);
                        String noidung = post.getString("noi_dung");
                        String avatarUrl = post.getString("url_anh_dai_dien");
                        String urlPost = post.getString("url");
                        String tenNguoiDung = post.getString("ho_ten");
                        String ngayBaiViet = post.getString("ngay_tao");
                        int id = post.getInt("bai_viet_id");
                        int idNhom = post.isNull("nhom_id") ? -1 : post.getInt("nhom_id");

                        PostItem postItem = new PostItem(noidung, tenNguoiDung, avatarUrl, ngayBaiViet, urlPost, id, idNhom);
                        lstPostItem.add(postItem);
                    }
                }
                requireActivity().runOnUiThread(() -> {
                    mulAdapter.clear();
                    mulAdapter.addAll(lstPostItem);
                    mulAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(this.getContext(), "Lỗi tải bài đăng", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching posts", e);
                });
            } finally {
                executor.shutdown();
            }
        });
    }
}
