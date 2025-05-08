package com.example.socialmediaapp;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.util.Log;
//import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

public class ApiClient {
    private static final String BASE_URL = "https://lhtpc.site/";  // Base URL

//    private static final String LINE_FEED = "\r\n";
//    private static final String CHARSET = "UTF-8";

    // POST request with JSON data
    public static JSONObject post(String endpoint, JSONObject data) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            int code = conn.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            String response = result.toString().trim();
            Log.d("ApiClient", "Raw response: " + response);

            if (response.startsWith("{")) {
                return new JSONObject(response);
            } else {
                Log.e("ApiClient", "Unexpected non-JSON response: " + response);
                return null;
            }

        } catch (Exception e) {
            Log.e("ApiClient", "Exception: " + e.getMessage(), e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
