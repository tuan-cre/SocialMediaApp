package com.example.socialmediaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    EditText lblName_Profile, lblNgaySinh_Profile, lblGioiTinh_Profile
            , lblQueQuan_Profile, lblTrinhDo_Profile, lblTrangThai_Profile
            , edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    Button btnReturn, btnLuu, btnHuy, btnChinhSua_Profile, btnConfirmChangePass, btnCancelChangePass;
    ImageView img_Profile;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private UpLoadImg upLoadImg;
    private String uploadedImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lblName_Profile = findViewById(R.id.lblName_Profile);
        lblNgaySinh_Profile = findViewById(R.id.lblNgaySinh_Profile);
        lblGioiTinh_Profile = findViewById(R.id.lblGioiTinh_Profile);
        lblQueQuan_Profile = findViewById(R.id.lblQueQuan_Profile);
        lblTrinhDo_Profile = findViewById(R.id.lblTrinhDo_Profile);
        lblTrangThai_Profile = findViewById(R.id.lblTrangThai_Profile);
        btnReturn = findViewById(R.id.btnReturn);
        btnLuu = findViewById(R.id.btnLuu);
        btnHuy = findViewById(R.id.btnHuy);
        btnChinhSua_Profile = findViewById(R.id.btnChinhSua_Profile);
        img_Profile = findViewById(R.id.img_Profile);
        img_Profile.setEnabled(false);

        upLoadImg = new UpLoadImg(this);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        btnReturn.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, MainActivity2.class));
            finish();
        });

        btnLuu.setOnClickListener(v -> updateProfile(taiKhoanId));

        btnHuy.setOnClickListener(v -> {
            cancelEdit();
            fetchProfile(taiKhoanId);
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            img_Profile.setImageURI(selectedImageUri);

                            // Upload and get URL using UpLoadImg
                            upLoadImg.uploadImg(selectedImageUri, new UpLoadImg.UploadListener() {
                                @Override
                                public void onUploaded(String imageUrl) {
                                    uploadedImageUrl = imageUrl;
                                    Toast.makeText(Profile.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    Toast.makeText(Profile.this, "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );

        img_Profile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnChinhSua_Profile.setOnClickListener(v -> {
            btnChinhSua_Profile.setVisibility(View.INVISIBLE);
            btnLuu.setVisibility(View.VISIBLE);
            btnHuy.setVisibility(View.VISIBLE);
            lblName_Profile.setEnabled(true);
            lblNgaySinh_Profile.setEnabled(true);
            lblGioiTinh_Profile.setEnabled(true);
            lblQueQuan_Profile.setEnabled(true);
            lblTrinhDo_Profile.setEnabled(true);
            img_Profile.setEnabled(true);
        });

        if (taiKhoanId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchProfile(taiKhoanId);
    }

    private void fetchProfile(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);

                JSONObject response = ApiClient.post("get_profile.php", requestData);

                runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        JSONObject user = response.optJSONObject("user");
                        if (user != null) {
                            uploadedImageUrl = user.optString("url_anh_dai_dien", "");
                            if (!uploadedImageUrl.isEmpty()) {
//                                ApiClient.loadImageFromUrl(uploadedImageUrl, img_Profile);
                                upLoadImg.setImageToView(uploadedImageUrl, img_Profile);
                            } else {
                                img_Profile.setImageResource(R.drawable.default_profile_image);
                            }
                            lblName_Profile.setText(user.optString("ho_ten", ""));
                            lblNgaySinh_Profile.setText(user.optString("ngay_sinh", ""));
                            lblGioiTinh_Profile.setText(user.optString("gioi_tinh", ""));
                            lblQueQuan_Profile.setText(user.optString("que_quan", ""));
                            lblTrinhDo_Profile.setText(user.optString("trinh_do_hoc_van", ""));
                            lblTrangThai_Profile.setText(user.optString("trang_thai", ""));
                        }
                    } else {
                        Toast.makeText(Profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(Profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching profile", e);
                });
            }
        });
    }

    private void updateProfile(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("urlanhdaidien", uploadedImageUrl != null ? uploadedImageUrl : img_Profile.getTag() != null ? img_Profile.getTag().toString() : "");
                requestData.put("hoten", lblName_Profile.getText().toString());
                requestData.put("ngaysinh", lblNgaySinh_Profile.getText().toString());
                requestData.put("gioitinh", lblGioiTinh_Profile.getText().toString());
                requestData.put("quequan", lblQueQuan_Profile.getText().toString());
                requestData.put("trinhdo", lblTrinhDo_Profile.getText().toString());

                if (uploadedImageUrl != null) {
                    requestData.put("url_anh_dai_dien", uploadedImageUrl);
                }

                JSONObject response = ApiClient.post("update_profile.php", requestData);

                runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                        fetchProfile(taiKhoanId);
                        cancelEdit();
                    } else {
                        Toast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(Profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating profile", e);
                });
            }
        });
    }

    private void cancelEdit() {
        btnChinhSua_Profile.setVisibility(View.VISIBLE);
        btnLuu.setVisibility(View.INVISIBLE);
        btnHuy.setVisibility(View.INVISIBLE);
        lblName_Profile.setEnabled(false);
        lblNgaySinh_Profile.setEnabled(false);
        lblGioiTinh_Profile.setEnabled(false);
        lblQueQuan_Profile.setEnabled(false);
        lblTrinhDo_Profile.setEnabled(false);
        img_Profile.setEnabled(false);
    }
}
