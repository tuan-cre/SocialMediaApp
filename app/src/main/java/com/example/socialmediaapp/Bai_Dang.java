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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bai_Dang extends AppCompatActivity {
    private static final String TAG = "BaiDangActivity";
    Button btnTroVe;
    Button btnDangBai;
    EditText txtBaiDangText;
    AdapterPost adapterPost;
    MultiTypeAdapter madapterPost;
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

        // Ánh xạ view
        txtBaiDangText = findViewById(R.id.txtBaiDangText);
        btnTroVe = findViewById(R.id.btnTroVe);
        btnDangBai = findViewById(R.id.btnDangBai);
        listView = findViewById(R.id.listViewPost);

        // Chuyển về MainActivity2 khi bấm nút
        btnTroVe.setOnClickListener(v -> {
            Intent intent = new Intent(Bai_Dang.this, MainActivity2.class);
            startActivity(intent);
        });

        // Lấy id tài khoản từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        // Sự kiện đăng bài
        btnDangBai.setOnClickListener(v -> {
            String content = txtBaiDangText.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(Bai_Dang.this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                postBaiDang(taiKhoanId);
            }
        });

        // Khởi tạo adapter và gán cho ListView
//        adapterPost = new AdapterPost(Bai_Dang.this, R.layout.activity_post_list_view_item, new ArrayList<>());
//        listView.setAdapter(adapterPost);
        madapterPost = new MultiTypeAdapter(Bai_Dang.this, new ArrayList<>(),"Post");
        listView.setAdapter(madapterPost);

        // Tải dữ liệu bài viết
        loadPosts();
    }

    // Hàm tải danh sách bài đăng từ server
    private void loadPosts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<PostItem> lstPostItem = new ArrayList<>();
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

                        PostItem postItem = new PostItem(noidung, tenNguoiDung, avatarUrl, ngayBaiViet, "", id, idNhom, 0);
                        lstPostItem.add(postItem);
                    }
                }

                runOnUiThread(() -> {
                    madapterPost.clear();
                    madapterPost.addAll(lstPostItem);
                    madapterPost.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching posts", e);
                runOnUiThread(() ->
                        Toast.makeText(Bai_Dang.this, "Lỗi tải bài đăng", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Hàm gửi bài đăng mới lên server
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
                        txtBaiDangText.setText(""); // Clear input
                        loadPosts(); // Reload posts
                    } else {
                        Toast.makeText(Bai_Dang.this, "Failed to upload new post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(Bai_Dang.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error posting", e);
                });
            }
        });
    }
}
