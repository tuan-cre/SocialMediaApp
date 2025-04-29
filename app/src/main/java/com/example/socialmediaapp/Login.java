package com.example.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.txtTendangnhap);
        passwordEditText = findViewById(R.id.txtMatkhau);
        Button btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        // Handle insets (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat insetsCompat = insets;
            v.setPadding(insetsCompat.getSystemGestureInsets().left, insetsCompat.getSystemGestureInsets().top,
                    insetsCompat.getSystemGestureInsets().right, insetsCompat.getSystemGestureInsets().bottom);
            return insets;
        });

        btnLogin.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(ProgressBar.VISIBLE);

            // Call the login task
            login(username, password);
        });
    }

    private void login(String username, String password) {
        // Hash the password before sending it
        String hashedPassword = hashPassword(password);

        // Call the login task
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = performLoginRequest(username, hashedPassword);  // Send hashed password
            runOnUiThread(() -> {
                handleLoginResponse(result);
                progressBar.setVisibility(ProgressBar.INVISIBLE);  // Hide the progress bar after response
            });
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
            e.printStackTrace();
            return password;  // Fallback, return the password if hashing fails
        }
    }

    private String performLoginRequest(String username, String hashedPassword) {
        StringBuilder result = new StringBuilder();
        try {
            // Define the URL for your login endpoint
            URL url = new URL("https://lhtpc.site/login.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            // Prepare the data to send (ensure the fields are correctly named)
            JSONObject data = new JSONObject();
            data.put("ten_dang_nhap", username);  // Username field
            data.put("mat_khau", hashedPassword);  // Password (hashed) field

            // Write the JSON data to the output stream
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            // Check the response code from the server
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                result.append("Error: " + responseCode);
                return result.toString();
            }

            // Read the response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            result.append("Exception: " + e.getMessage());
        }
        return result.toString();
    }

    private void handleLoginResponse(String result) {
        try {
            JSONObject response = new JSONObject(result);
            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to MainActivity after successful login
                Intent intent = new Intent(Login.this, MainActivity2.class);
                startActivity(intent);
                finish();  // Optional: This will finish the Login activity, so the user can't navigate back to it.
            } else {
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Login.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
