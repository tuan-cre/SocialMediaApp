package com.example.socialmediaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bai_Dang extends AppCompatActivity {
    private static final String TAG = "BaiDangActivity";
    Button btnTroVe;
    Button btnDangBai;
    EditText txtBaiDangText;
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
        btnTroVe.setOnClickListener(v -> {
            Intent intent = new Intent(Bai_Dang.this, MainActivity2.class);
            startActivity(intent);
        });

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        btnDangBai.setOnClickListener(v -> {
            postBaiDang(taiKhoanId);
        });

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
//                        fetchProfile(taiKhoanId);
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