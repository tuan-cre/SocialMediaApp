package com.example.socialmediaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragment_profile  extends Fragment {
    private static final String TAG = "ProfileFragment";

    EditText edtName, edtDateOfBirth, edtGender, edtProvince, edtEducationLevel;
    Button btnSaveInfo, btnCancelEdit, btnEditInfo;
    ImageView imgProfilePicture;
    LinearLayout lloButtonEdit;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private UpLoadImg upLoadImg;
    private String uploadedImageUrl = null;

    private int taiKhoanId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        edtName = view.findViewById(R.id.edtName);
        edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);
        edtGender = view.findViewById(R.id.edtGender);
        edtProvince = view.findViewById(R.id.edtProvince);
        edtEducationLevel = view.findViewById(R.id.edtEducationLevel);
//        lblTrangThai_Profile = view.findViewById(R.id.lblTrangThai_Profile);
        btnSaveInfo = view.findViewById(R.id.btnSaveInfo);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnEditInfo = view.findViewById(R.id.btnEditInfo);
        imgProfilePicture = view.findViewById(R.id.imgProfilePicture);
        lloButtonEdit = view.findViewById(R.id.lloButtonEdit);
        imgProfilePicture.setEnabled(false);

        upLoadImg = new UpLoadImg(getContext());

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        if (taiKhoanId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        fetchProfile(taiKhoanId);

        btnSaveInfo.setOnClickListener(v -> updateProfile(taiKhoanId));

        btnCancelEdit.setOnClickListener(v -> {
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
                            imgProfilePicture.setImageURI(selectedImageUri);

                            upLoadImg.uploadImg(selectedImageUri, new UpLoadImg.UploadListener() {
                                @Override
                                public void onUploaded(String imageUrl) {
                                    uploadedImageUrl = imageUrl;
                                    Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailed(String error) {
                                    Toast.makeText(getContext(), "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );

        imgProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnEditInfo.setOnClickListener(v -> {
            edtName.setEnabled(true);
            edtDateOfBirth.setEnabled(true);
            edtGender.setEnabled(true);
            edtProvince.setEnabled(true);
            edtEducationLevel.setEnabled(true);
            imgProfilePicture.setEnabled(true);
            btnEditInfo.setVisibility(View.INVISIBLE);
            btnSaveInfo.setVisibility(View.VISIBLE);
            btnCancelEdit.setVisibility(View.VISIBLE);
            btnEditInfo.setVisibility(View.GONE);
            lloButtonEdit.setVisibility(View.VISIBLE);
        });

        return view;
    }

    private void fetchProfile(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);

                JSONObject response = ApiClient.post("get_profile.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        JSONObject user = response.optJSONObject("user");
                        if (user != null) {
                            uploadedImageUrl = user.optString("url_anh_dai_dien", "");
                            if (!uploadedImageUrl.isEmpty()) {
                                upLoadImg.setImageToView(uploadedImageUrl, imgProfilePicture);
                            } else {
                                imgProfilePicture.setImageResource(R.drawable.default_profile_image);
                            }
                            edtName.setText(user.optString("ho_ten", ""));
                            edtDateOfBirth.setText(user.optString("ngay_sinh", ""));
                            edtGender.setText(user.optString("gioi_tinh", ""));
                            edtProvince.setText(user.optString("que_quan", ""));
                            edtEducationLevel.setText(user.optString("trinh_do_hoc_van", ""));
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                requestData.put("urlanhdaidien", uploadedImageUrl != null ? uploadedImageUrl : imgProfilePicture.getTag() != null ? imgProfilePicture.getTag().toString() : "");
                requestData.put("hoten", edtName.getText().toString());
                requestData.put("ngaysinh", edtDateOfBirth.getText().toString());
                requestData.put("gioitinh", edtGender.getText().toString());
                requestData.put("quequan", edtProvince.getText().toString());
                requestData.put("trinhdo", edtEducationLevel.getText().toString());

                if (uploadedImageUrl != null) {
                    requestData.put("url_anh_dai_dien", uploadedImageUrl);
                }

                JSONObject response = ApiClient.post("update_profile.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        cancelEdit();
                    } else {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating profile", e);
                });
            }
        });
    }

    private void cancelEdit() {
        edtName.setEnabled(false);
        edtDateOfBirth.setEnabled(false);
        edtGender.setEnabled(false);
        edtProvince.setEnabled(false);
        edtEducationLevel.setEnabled(false);
        imgProfilePicture.setEnabled(false);
        lloButtonEdit.setVisibility(View.GONE);
        btnEditInfo.setVisibility(View.VISIBLE);
    }
}
