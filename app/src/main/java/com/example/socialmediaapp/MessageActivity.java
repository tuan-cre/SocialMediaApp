package com.example.socialmediaapp;




import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.json.JSONObject;


public class MessageActivity extends AppCompatActivity {

    private ProgressBar progress;

    private final Handler handler = new Handler();
    private final int POLL_INTERVAL = 3000;
    private int id_nguoi_gui;
    private int id_nguoi_nhan;

    private RecyclerView rcMessage;
    private static final String TAG = "MessageActivity";
    private MessageAdapter messageAdapter;
    private List<Message> lstMessage;

    private EditText txtMessage;
    private static final String BASE_URL = "http://localhost/";
    private Button btnSend;
    private boolean isUserJustSentMessage = true;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            boolean isKeyboardVisible = imeInsets.bottom > 0;

            if (isKeyboardVisible) {
                v.setPadding(0, 0, 0, imeInsets.bottom); // Chỉ set padding khi bàn phím mở
            } else {
                v.setPadding(0, 0, 0, 90); // Reset padding nếu bàn phím đóng
            }

            return insets;
        });

        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        rcMessage = findViewById(R.id.rcv_message);
        progress = findViewById(R.id.progressMessage);
        id_nguoi_gui = getIntent().getIntExtra("id_nguoi_gui", 0);
        id_nguoi_nhan = getIntent().getIntExtra("id_nguoi_nhan", 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcMessage.setLayoutManager(linearLayoutManager);

        lstMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter(lstMessage);
        rcMessage.setAdapter(messageAdapter);
        progress.setVisibility(View.VISIBLE);
        loadMesage();
 //       rcMessage.scrollToPosition(lstMessage.size()-1);
        progress.setVisibility(View.INVISIBLE);

        handler.post(pollingRunnable);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void loadMesage() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            lstMessage = new ArrayList<>();
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", id_nguoi_gui);
                requestData.put("nguoi_nhan_id", id_nguoi_nhan);

                JSONObject response = ApiClient.post("get_messages.php", requestData);
                if (response != null && response.optBoolean("success", false)) {
                    List<Message> tempList = new ArrayList<>();
                    for (int i = 0; i < response.getJSONArray("messages").length(); i++) {
                        JSONObject messages = response.getJSONArray("messages").getJSONObject(i);

                        Message message;
                        int id_chu_tn = messages.getInt("nguoi_gui_id");
                        String noidung = messages.getString("noi_dung");
                        if(id_nguoi_gui==id_chu_tn)
                            message = new Message(noidung,true);
                        else
                            message = new Message(noidung,false);
                        tempList.add(message);
                    }
                    runOnUiThread(() -> {
                        lstMessage.clear();
                        lstMessage.addAll(tempList);
                        messageAdapter.setData(lstMessage);
                        messageAdapter.notifyDataSetChanged();
                        if(isUserJustSentMessage){
                            rcMessage.scrollToPosition(lstMessage.size()-1);
                            isUserJustSentMessage = false;
                        }

                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching posts", e);
                runOnUiThread(() ->
                        Toast.makeText(MessageActivity.this, "Lỗi tải bài đăng", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void sendMessage() {
        progress.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", id_nguoi_gui);
                requestData.put("nguoi_nhan_id", id_nguoi_nhan);
                requestData.put("noi_dung", txtMessage.getText().toString());
                Log.d(TAG, "Sending message: " + requestData.toString());

                JSONObject response = ApiClient.post("send_messages.php", requestData);

                runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(MessageActivity.this, "Post successfully", Toast.LENGTH_SHORT).show();
                        txtMessage.setText(""); // Clear input
                        isUserJustSentMessage = true;
                        loadMesage();
                    } else {
                        Toast.makeText(MessageActivity.this, "Failed to upload new post", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MessageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error posting", e);
                });
            }
            finally {
                // Ẩn ProgressBar sau khi hoàn thành công việc
                runOnUiThread(() -> progress.setVisibility(View.INVISIBLE));
            }
        });
    }
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            loadMesage(); // gọi API để tải tin nhắn
            handler.postDelayed(this, POLL_INTERVAL); // lặp lại
        }
    };
    protected void onDestroy() {
        super.onDestroy();

        // Ngắt polling hoặc các tài nguyên khác
        handler.removeCallbacks(pollingRunnable);
    }
}


