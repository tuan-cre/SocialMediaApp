package com.example.socialmediaapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String BASE_URL = "https://lhtpc.site/";  // Base URL
    private static final String LINE_FEED = "\r\n";
    private static final String CHARSET = "UTF-8";

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


    // File upload method with error handling and proper response handling
    public static void uploadFile(String endpoint, File file, int nguoiDungId) {
        String boundary = "===" + System.currentTimeMillis() + "===";
        String LINE_FEED = "\r\n";

        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            DataOutputStream request = new DataOutputStream(conn.getOutputStream());

            // 🧑 Gửi ID người dùng
            request.writeBytes("--" + boundary + LINE_FEED);
            request.writeBytes("Content-Disposition: form-data; name=\"nguoi_dung_id\"" + LINE_FEED);
            request.writeBytes("Content-Type: text/plain; charset=" + CHARSET + LINE_FEED);
            request.writeBytes(LINE_FEED);
            request.writeBytes(String.valueOf(nguoiDungId) + LINE_FEED);

            // 🖼️ Gửi file ảnh
            request.writeBytes("--" + boundary + LINE_FEED);
            request.writeBytes("Content-Disposition: form-data; name=\"profile_image\"; filename=\"" + file.getName() + "\"" + LINE_FEED);
            request.writeBytes("Content-Type: image/jpeg" + LINE_FEED);
            request.writeBytes(LINE_FEED);

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                request.write(buffer, 0, bytesRead);
            }
            inputStream.close();

            request.writeBytes(LINE_FEED);
            request.writeBytes("--" + boundary + "--" + LINE_FEED);
            request.flush();
            request.close();

            int responseCode = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Log.d("ApiClient", "Upload response (" + responseCode + "): " + response);

        } catch (Exception e) {
            Log.e("ApiClient", "Upload error: " + e.getMessage(), e);
        }
    }
    public static void loadImageFromUrl(String imageUrl, ImageView imageView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                imageView.post(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                Log.e("ApiClient", "Image load error: " + e.getMessage(), e);
            }
        });
    }
}
