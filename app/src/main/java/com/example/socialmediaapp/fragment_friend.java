package com.example.socialmediaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

public class fragment_friend extends Fragment implements MultiTypeAdapter.OnFriendActionListener {

    private static final String TAG = "FragmentFriend";

    EditText edtFriendID;
    Button btnSendInvite;
    private int taiKhoanId;

    ListView listViewInvite, listViewFriend;
    MultiTypeAdapter adapterInvite, adapterFriend;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        edtFriendID = view.findViewById(R.id.edtFriendID);
        btnSendInvite = view.findViewById(R.id.btnSendInvite);
        listViewInvite = view.findViewById(R.id.listViewInvite);
        listViewFriend = view.findViewById(R.id.listViewFriend);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);

        adapterInvite = new MultiTypeAdapter(getContext(), new ArrayList<>(), "Invite", this);
        adapterFriend = new MultiTypeAdapter(getContext(), new ArrayList<>(), "Friend", this);

        listViewInvite.setAdapter(adapterInvite);
        listViewFriend.setAdapter(adapterFriend);

        btnSendInvite.setOnClickListener(v -> sendFriendInvite(taiKhoanId));

        // Set long click listener ONCE here
        listViewFriend.setOnItemLongClickListener((parent, view1, position, id) -> {
            FriendItem friendItem = (FriendItem) adapterFriend.getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Nhắn tin cho bạn bè");
            builder.setMessage("Bạn muốn nhắn tin cho " + friendItem.getTen_ban_be() + "?");
            builder.setPositiveButton("Đồng ý", (dialog, which) -> {
                Intent intent = new Intent(requireContext(), MessageActivity.class);
                intent.putExtra("id_nguoi_gui", taiKhoanId);
                int id_nguoi_nhan = (friendItem.getNguoi_dung_id() == taiKhoanId)
                        ? friendItem.getBan_be_id() : friendItem.getNguoi_dung_id();
                intent.putExtra("id_nguoi_nhan", id_nguoi_nhan);
                startActivity(intent);
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

        loadFriendInvites();
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
                        loadFriendInvites();
                    } else {
                        Toast.makeText(requireContext(), "Mời bạn bè thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e(TAG, "Lỗi khi gửi lời mời", e);
            }
        });
    }

    private void loadFriendInvites() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            ArrayList<Object> lstInviteItem = new ArrayList<>();
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

                        String urlAvatar = "";
                        String ten_ban_be = "";

                        if (nguoi_dung_id == taiKhoanId) {
                            urlAvatar = post.optString("anh_nguoi_nhan", "");
                            ten_ban_be = post.optString("ten_nguoi_nhan", "");
                        } else {
                            urlAvatar = post.optString("anh_nguoi_gui", "");
                            ten_ban_be = post.optString("ten_nguoi_gui", "");
                        }

                        FriendItem friendItem = new FriendItem(nguoi_dung_id, ban_be_id, urlAvatar, trang_thai, ngay_tao, ten_ban_be);

                        if (trang_thai.equals("đồng ý")) {
                            lstFriendItem.add(friendItem);
                        } else {
                            if (ban_be_id == taiKhoanId) {
                                lstInviteItem.add(friendItem);
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    adapterInvite.clear();
                    adapterInvite.addAll(lstInviteItem);
                    adapterInvite.notifyDataSetChanged();

                    adapterFriend.clear();
                    adapterFriend.addAll(lstFriendItem);
                    adapterFriend.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e(TAG, "Lỗi tải danh sách bạn bè", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi tải danh sách bạn bè", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onFriendAction(FriendItem item, String action) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", item.getNguoi_dung_id());
                requestData.put("ban_be_id", item.getBan_be_id());
                requestData.put("trang_thai", action);

                Log.d(TAG, "Gửi dữ liệu cập nhật: " + requestData.toString());
                JSONObject response = ApiClient.post("update_friend_status.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(requireContext(), "Đã " + (action.equals("đồng ý") ? "chấp nhận" : "từ chối"), Toast.LENGTH_SHORT).show();
                        loadFriendInvites();
                    } else {
                        Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e(TAG, "Lỗi cập nhật trạng thái bạn bè", e);
            }
        });
    }
}
