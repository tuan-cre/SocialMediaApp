package com.example.socialmediaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgetPassword extends AppCompatActivity {
    private static final String TAG = "ForgetPasswordActivity";
    EditText txtRecoverEmail;
    TextView txtBackToLogin;
    Button btnRecoverPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtRecoverEmail = findViewById(R.id.txtEmailForget);
        btnRecoverPassword = findViewById(R.id.btnForgetPass);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);
        txtBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgetPassword.this, Login.class));
            finish();
        });

        btnRecoverPassword.setOnClickListener( v -> recoverPassword());
    }

    private void recoverPassword() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("email", txtRecoverEmail.getText().toString().trim());
                Log.d(TAG, "Request data: " + requestData.toString());

                JSONObject response = ApiClient.post("recover_password.php", requestData);

                runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(ForgetPassword.this, "Password has been send to you email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgetPassword.this, "Failed to recover password", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(ForgetPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error recovery password", e);
                });
            }
        });
    }
}