package com.example.socialmediaapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragment_friend extends Fragment {

    private static final String TAG = "FragmentFriend";

    EditText edtFriendID;
    Button btnSendInvite;
    private int taiKhoanId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        edtFriendID = view.findViewById(R.id.edtFriendID);
        btnSendInvite = view.findViewById(R.id.btnSendInvite);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        btnSendInvite.setOnClickListener(v -> sendFriendInvite(taiKhoanId));

        return view;
    }

    private void sendFriendInvite(int taiKhoanId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("banbeid", edtFriendID.getText().toString());

                JSONObject response = ApiClient.post("invite_friend.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(requireContext(), "Invite successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to invite friend", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error sending invite", e);
                });
            }
        });
    }
}
