package com.example.socialmediaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bai_Dang extends AppCompatActivity {
    private static final String TAG = "BaiDangActivity";
    Button btnTroVe;
    Button btnDangBai;
    EditText txtBaiDangText;
    AdapterPost adapterPost = null;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bai_dang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtBaiDangText = findViewById(R.id.txtBaiDangText);
        btnTroVe = findViewById(R.id.btnTroVe);
        btnDangBai = findViewById(R.id.btnDangBai);
        listView = findViewById(R.id.listViewPost);

        btnTroVe.setOnClickListener(v -> {
            Intent intent = new Intent(Bai_Dang.this, MainActivity2.class);
            startActivity(intent);
        });

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        btnDangBai.setOnClickListener(v -> {
            String content = txtBaiDangText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(Bai_Dang.this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                postBaiDang(taiKhoanId);
            }
        });

        adapterPost = new AdapterPost(Bai_Dang.this, R.layout.activity_post_list_view_item, new ArrayList<>());
        listView.setAdapter(adapterPost);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ArrayList<PostItem> listPostItem = getDSPost();
                runOnUiThread(() -> {
                    adapterPost.addAll(listPostItem);
                    adapterPost.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching posts", e);
            }
        });
    }

    private ArrayList<PostItem> getDSPost() {
        ArrayList<PostItem> listPostItem = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", 1);
                JSONObject response = ApiClient.post("get_Post.php", requestData);
                if (response != null && response.optBoolean("success", false)) {
                    for (int i = 0; i < response.getJSONArray("posts").length(); i++) {
                        JSONObject post = response.getJSONArray("posts").getJSONObject(i);
                        String noidung = post.getString("noi_dung");
                        String avatarUrl = post.getString("url_anh_dai_dien");
                        String tenNguoiDung = post.getString("ho_ten");
                        String ngayBaiViet = post.getString("ngay_tao");
                        int id = post.getInt("bai_viet_id");
                        int idNhom = post.isNull("nhom_id") ? -1 : post.getInt("nhom_id");

                        PostItem postItem = new PostItem(noidung, tenNguoiDung, avatarUrl, ngayBaiViet, id, idNhom);
                        listPostItem.add(postItem);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching posts", e);
            }
        });
        return listPostItem;
    }

    private void postBaiDang(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("noidung", txtBaiDangText.getText().toString());

                JSONObject response = ApiClient.post("new_post.php", requestData);

                runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(Bai_Dang.this, "Post successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Bai_Dang.this, "Failed to upload new post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(Bai_Dang.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating profile", e);
                });
            }
        });
    }
}

