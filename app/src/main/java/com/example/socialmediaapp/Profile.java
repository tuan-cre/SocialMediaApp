package com.example.socialmediaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    TextView lblName_Profile, lblNgaySinh_Profile, lblGioiTinh_Profile, lblQueQuan_Profile, lblTrinhDo_Profile, lblTrangThai_Profile;
    Button btnReturn;

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

        btnReturn.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, MainActivity2.class));
            finish();
        });


        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int taiKhoanId = prefs.getInt("tai_khoan_id", -1);

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
                            lblName_Profile.setText(user.optString("ho_ten", ""));
                            lblNgaySinh_Profile.setText(user.optString("ngay_sinh", ""));
                            lblGioiTinh_Profile.setText(user.optString("gioi_tinh", ""));
                            lblQueQuan_Profile.setText(user.optString("que_quan", ""));
                            lblTrinhDo_Profile.setText(user.optString("trinh_do", ""));
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
}
