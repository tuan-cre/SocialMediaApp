package com.example.socialmediaapp;

import android.content.Intent;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText fullNameEditText, usernameEditText, emailEditText, passwordEditText;
    private ProgressBar progressBar;
    private TextView txtBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        fullNameEditText = findViewById(R.id.txtHoTen);
        usernameEditText = findViewById(R.id.txtTendangnhap);
        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtMatkhau);
        Button btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar2);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

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

        txtBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, Login.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Register.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(ProgressBar.VISIBLE);
            registerUser(fullName, username, email, password);
        });
    }

    private void registerUser(String fullName, String username, String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("hoten", fullName);
                requestData.put("username", username);
                requestData.put("email", email);
                requestData.put("password", password);

                JSONObject response = ApiClient.post("register.php", requestData);

                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    handleRegisterResponse(response);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Log.e(TAG, "Registration error", e);
                    Toast.makeText(Register.this, "Lỗi đăng ký: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleRegisterResponse(JSONObject response) {
        if (response == null) {
            Log.e(TAG, "No response from server");
            Toast.makeText(this, "Không có phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String status = response.getString("status");
            String message = response.getString("message");

            if (status.equals("success")) {
                Log.i(TAG, "Registration successful");
                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();

                // Automatically login after successful registration
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                Intent intent = new Intent(Register.this, Login.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();
            } else {
                Log.w(TAG, "Registration failed: " + message);
                Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Response parsing error", e);
            Toast.makeText(Register.this, "Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
        }
    }
}