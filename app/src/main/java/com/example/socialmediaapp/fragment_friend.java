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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragment_friend extends Fragment {

    private static final String TAG = "FragmentFriend";

    EditText edtFriendID;
    Button btnSendInvite;
    private int taiKhoanId;

    ListView listViewInvite;
    MultiTypeAdapter adapterInvite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        edtFriendID = view.findViewById(R.id.edtFriendID);
        btnSendInvite = view.findViewById(R.id.btnSendInvite);
        listViewInvite = view.findViewById(R.id.listViewInvite);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        adapterInvite = new MultiTypeAdapter(getContext(), new ArrayList<>());
        listViewInvite.setAdapter(adapterInvite);

        btnSendInvite.setOnClickListener(v -> sendFriendInvite(taiKhoanId));

        loadFriendInvites(); // gọi khi fragment khởi tạo

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
                        Toast.makeText(requireContext(), "Mời bạn bè thành công", Toast.LENGTH_SHORT).show();
                        loadFriendInvites(); // refresh lại danh sách
                    } else {
                        Toast.makeText(requireContext(), "Mời bạn bè thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Lỗi khi gửi lời mời", e);
                });
            }
        });
    }

    private void loadFriendInvites() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<Object> lstFriendItem = new ArrayList<>();
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);

                JSONObject response = ApiClient.post("get_friend.php", requestData);
                if (response != null && response.optBoolean("success", false)) {
                    JSONArray postsArray = response.getJSONArray("posts");
                    for (int i = 0; i < postsArray.length(); i++) {
                        JSONObject post = postsArray.getJSONObject(i);
                        int nguoi_dung_id = post.getInt("nguoi_dung_id");
                        int ban_be_id = post.getInt("ban_be_id");
                        String trang_thai = post.getString("trang_thai");
                        String ngay_tao = post.getString("ngay_tao");

                        FriendItem friendItem = new FriendItem(nguoi_dung_id, ban_be_id, trang_thai, ngay_tao);
                        lstFriendItem.add(friendItem);
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    adapterInvite.clear();
                    adapterInvite.addAll(lstFriendItem);
                    adapterInvite.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "Lỗi tải danh sách bạn bè", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi tải danh sách bạn bè", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
