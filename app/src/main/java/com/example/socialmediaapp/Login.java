package com.example.socialmediaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText usernameEditText, passwordEditText;
    private ProgressBar progressBar;
    private TextView txtCreateNewAccount, txtForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isLoggedIn()) {
            // Already logged in, skip login screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.txtTendangnhap);
        passwordEditText = findViewById(R.id.txtMatkhau);
        Button btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtCreateNewAccount = findViewById(R.id.txtCreateNewAccount);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);

        // Handle insets (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        txtCreateNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });

        txtForgetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, ForgetPassword.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(ProgressBar.VISIBLE);
            login(username, password);
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void saveSession(int taiKhoanId, String URLAvatar) {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("isLoggedIn", true)
                .putInt("tai_khoan_id", taiKhoanId)
                .putString("url_anh_dai_dien", URLAvatar)
                .apply();
    }

    private void login(String username, String password) {
        String hashedPassword = hashPassword(password);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("ten_dang_nhap", username);
                requestData.put("mat_khau", hashedPassword);

                JSONObject response = ApiClient.post("login1.php", requestData);

                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    handleLoginResponse(response);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            return password;  // Fallback
        }
    }

    private void handleLoginResponse(JSONObject response) {
        if (response == null) {
            Log.e(TAG, "No response from server");
            Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                // Extract user ID
                int taiKhoanId = response.getInt("tai_khoan_id");
                String url_Anh_dai_dien = response.getString("url_anh_dai_dien");

                // Save session (logged in state + ID + url_Anh_dai_dien)
                saveSession(taiKhoanId, url_Anh_dai_dien);

                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                // Proceed to the main activity
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.w(TAG, "Login failed: " + message);
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Response parsing error", e);
            Toast.makeText(Login.this, "Response error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
