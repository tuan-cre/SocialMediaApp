package com.example.socialmediaapp;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class fragment_profile  extends Fragment {
    private static final String TAG = "ProfileFragment";
    EditText edtName, edtDateOfBirth, edtGender, edtProvince, edtEducationLevel,
    edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    Button btnSaveInfo, btnCancelEdit, btnEditInfo, btnConfirmChangePass, btnCancelChangePass, btnChangePass;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale;
    ImageView imgProfilePicture;
    LinearLayout lloButtonEdit, lloButtonChangePass, lloInfo, lloPassField, lloTwoButton;

    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private UpLoadImg upLoadImg;
    private String uploadedImageUrl = null;
    private int taiKhoanId;
    ListView listViewHistory;
    MultiTypeAdapter adapterHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        edtName = view.findViewById(R.id.edtName);
        edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);
        edtGender = view.findViewById(R.id.edtGender);
        edtProvince = view.findViewById(R.id.edtProvince);
        edtEducationLevel = view.findViewById(R.id.edtEducationLevel);
        btnSaveInfo = view.findViewById(R.id.btnSaveInfo);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnEditInfo = view.findViewById(R.id.btnEditInfo);
        imgProfilePicture = view.findViewById(R.id.imgProfilePicture);
        lloButtonEdit = view.findViewById(R.id.lloButtonEdit);
        imgProfilePicture.setEnabled(false);
        rgGender = view.findViewById(R.id.rgGender);
        rbMale = view.findViewById(R.id.rdMale);
        rbFemale = view.findViewById(R.id.rdFemale);
        edtOldPassword = view.findViewById(R.id.edtOldPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = view.findViewById(R.id.edtConfirmNewPassword);
        btnConfirmChangePass = view.findViewById(R.id.btnConfirmChangePass);
        btnCancelChangePass = view.findViewById(R.id.btnCancelChangePass);
        lloButtonChangePass = view.findViewById(R.id.lloButtonChangePass);
        lloPassField = view.findViewById(R.id.lloPassField);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        lloInfo = view.findViewById(R.id.lloInfo);
        lloTwoButton = view.findViewById(R.id.lloTwoButton);
        listViewHistory = view.findViewById(R.id.listViewHistory);

        upLoadImg = new UpLoadImg(getContext());

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);
        if (taiKhoanId == -1) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }
        fetchProfile(taiKhoanId);

        loadHistory(taiKhoanId);
        adapterHistory = new MultiTypeAdapter(getContext(), new ArrayList<>(), "Post");
        listViewHistory.setAdapter(adapterHistory);


//        if (!hasLoaded) {
//            hasLoaded = true;
//            fetchProfile(taiKhoanId);
//        }
//        fetchProfile(taiKhoanId);

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

                            // Log the selected image URI
                            Log.d(TAG, "Image URI selected: " + selectedImageUri.toString());

                            imgProfilePicture.setImageURI(selectedImageUri);

                            // Check if the image is being picked correctly
                            if (selectedImageUri != null) {
                                Log.d(TAG, "Image picked successfully.");
                            } else {
                                Log.d(TAG, "Image picking failed.");
                            }

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
                        } else {
                            Log.d(TAG, "No image data received.");
                        }
                    } else {
                        Log.d(TAG, "Image picking cancelled or failed.");
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
            edtDateOfBirth.setFocusable(false);
            edtDateOfBirth.setClickable(true);

            edtDateOfBirth.setOnClickListener(dateView -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                            edtDateOfBirth.setText(formattedDate);
                        },
                        year, month, day
                );
                datePickerDialog.show();
            });
            rgGender.setVisibility(View.VISIBLE);
            edtGender.setVisibility(View.GONE);
            if (edtGender.getText().toString().equals("Nam")) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
            edtGender.setEnabled(true);
            edtProvince.setEnabled(true);
            edtEducationLevel.setEnabled(true);
            imgProfilePicture.setEnabled(true);

            lloTwoButton.setVisibility(View.GONE);
            btnSaveInfo.setVisibility(View.VISIBLE);
            btnCancelEdit.setVisibility(View.VISIBLE);
            lloButtonEdit.setVisibility(View.VISIBLE);
        });

        btnChangePass.setOnClickListener(v -> {
            lloInfo.setVisibility(View.GONE);
            lloPassField.setVisibility(View.VISIBLE);
            lloButtonChangePass.setVisibility(View.VISIBLE);
            lloTwoButton.setVisibility(View.GONE);
            edtOldPassword.setEnabled(true);
            edtNewPassword.setEnabled(true);
            edtConfirmNewPassword.setEnabled(true);
        });

        btnCancelChangePass.setOnClickListener(v -> {
            lloInfo.setVisibility(View.VISIBLE);
            lloPassField.setVisibility(View.GONE);
            lloButtonChangePass.setVisibility(View.GONE);
            lloTwoButton.setVisibility(View.VISIBLE);
        });

        btnConfirmChangePass.setOnClickListener(v -> { changePassword(taiKhoanId);});
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
            } finally {
                executor.shutdown();
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
//                Log.d(TAG, "Date of birth: " + edtDateOfBirth.getText().toString());
                requestData.put("ngaysinh", edtDateOfBirth.getText().toString());
//                requestData.put("gioitinh", edtGender.getText().toString());
                if (rbMale.isChecked()) {
                    requestData.put("gioitinh", "Nam");
                } else if (rbFemale.isChecked()) {
                    requestData.put("gioitinh", "Nữ");
                }
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
        edtGender.setVisibility(View.VISIBLE);
        edtGender.setEnabled(false);
        rgGender.setVisibility(View.GONE);
        edtProvince.setEnabled(false);
        edtEducationLevel.setEnabled(false);
        imgProfilePicture.setEnabled(false);
        lloButtonEdit.setVisibility(View.GONE);
        lloTwoButton.setVisibility(View.VISIBLE);
        fetchProfile(taiKhoanId);
    }

    private void changePassword(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String oldHashedPassword = hashPassword(edtOldPassword.getText().toString());
                String newHashedPassword = hashPassword(edtNewPassword.getText().toString());
                String confirmNewHashedPassword = hashPassword(edtConfirmNewPassword.getText().toString());

                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData
                        .put("old_password", oldHashedPassword)
                        .put("new_password", newHashedPassword)
                        .put("confirm_new_password", confirmNewHashedPassword);

                Log.d(TAG, "Request data: " + requestData.toString());
                JSONObject response = ApiClient.post("change_password.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        edtOldPassword.setText("");
                        edtNewPassword.setText("");
                        edtConfirmNewPassword.setText("");
                        lloInfo.setVisibility(View.VISIBLE);
                        lloPassField.setVisibility(View.GONE);
                        lloButtonChangePass.setVisibility(View.GONE);
                        btnEditInfo.setVisibility(View.VISIBLE);
                        btnChangePass.setVisibility(View.VISIBLE);
                    } else {
                        String errorMsg = response.optString("message", "Failed to change password");
                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error changing password", e);
                });
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Password hashing failed", e);
            return password;  // Fallback (should never happen for SHA-256)
        }
    }

    private void loadHistory(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<PostItem> lstPostItem = new ArrayList<PostItem>();
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);

                JSONObject response = ApiClient.post("get_post_history.php", requestData);

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

                        PostItem postItem = new PostItem(noidung, tenNguoiDung, avatarUrl, ngayBaiViet, urlPost, id, idNhom, 0);
                        lstPostItem.add(postItem);
                    }
                }
                requireActivity().runOnUiThread(() -> {
                    adapterHistory.clear();
                    adapterHistory.addAll(lstPostItem);
                    adapterHistory.notifyDataSetChanged();
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(this.getContext(), "Lỗi tải bài đăng", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching posts", e);
                });
            }
            finally {
                executor.shutdown();
            }
        });
    }
}
