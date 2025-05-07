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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class fragment_friend extends Fragment implements MultiTypeAdapter.OnFriendActionListener {

    private static final String TAG = "FragmentFriend";

    EditText edtFriendID;
    Button btnSendInvite;
    private int taiKhoanId;

    ListView listViewInvite, listViewFriend, listView;
    MultiTypeAdapter adapterInvite, adapterFriend, adapter;
    private ArrayList<Object> results = new ArrayList<>();
    SearchView searchView;
    private boolean hasLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        //edtFriendID = view.findViewById(R.id.edtFriendID);
        //btnSendInvite = view.findViewById(R.id.btnSendInvite);
        searchView = view.findViewById(R.id.searchView);
        listViewInvite = view.findViewById(R.id.listViewInvite);
        listViewFriend = view.findViewById(R.id.listViewFriend);
        listView = view.findViewById(R.id.listViewUsers);

        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Activity.MODE_PRIVATE);
        taiKhoanId = prefs.getInt("tai_khoan_id", -1);
        if (taiKhoanId == -1) {
            if (getContext() != null) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            }
            return view;
        }
        if (!hasLoaded) {
            loadFriendInvites();
        }

        adapterInvite = new MultiTypeAdapter(getContext(), new ArrayList<>(), "Invite", this);
        adapterFriend = new MultiTypeAdapter(getContext(), new ArrayList<>(), "Friend", this);


        //listView.setAdapter(adapter);
        setupSearch();
        listViewInvite.setAdapter(adapterInvite);
        listViewFriend.setAdapter(adapterFriend);

        //btnSendInvite.setOnClickListener(v -> sendFriendInvite(taiKhoanId));

        // Set long click listener ONCE here
        listViewFriend.setOnItemLongClickListener((parent, view1, position, id) -> {
            FriendItem friendItem = (FriendItem) adapterFriend.getItem(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Chọn hành động");
            String[] options = {"Nhắn tin", "Xóa bạn"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    // Nhắn tin
                    Intent intent = new Intent(requireContext(), MessageActivity.class);
                    intent.putExtra("id_nguoi_gui", taiKhoanId);
                    int id_nguoi_nhan = (friendItem.getNguoi_dung_id() == taiKhoanId)
                            ? friendItem.getBan_be_id() : friendItem.getNguoi_dung_id();
                    intent.putExtra("id_nguoi_nhan", id_nguoi_nhan);
                    startActivity(intent);
                } else if (which == 1) {
                    // Xóa bạn
                    int friendId = (friendItem.getNguoi_dung_id() == taiKhoanId)
                            ? friendItem.getBan_be_id() : friendItem.getNguoi_dung_id();
                    deleteFriend(taiKhoanId, friendId);
                }
            });
            builder.show();
            return true;
        });

//        if (!hasLoaded) {
//            hasLoaded = true;
//            loadFriendInvites();
//        }
//        loadFriendInvites();
        return view;
    }

    public void sendFriendInvite(int taiKhoanId) {
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

    private void deleteFriend(int taiKhoanId, int friendId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("banbeid", friendId);

                JSONObject response = ApiClient.post("delete_friend.php", requestData);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.optBoolean("success", false)) {
                        Toast.makeText(requireContext(), "Xóa bạn bè thành công", Toast.LENGTH_SHORT).show();
                        loadFriendInvites();
                    } else {
                        Toast.makeText(requireContext(), "Xóa bạn bè thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                Log.e(TAG, "Lỗi khi xóa bạn bè", e);
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
            } finally {
                hasLoaded = true;
                executor.shutdown();
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

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchUsers(query);
                    listView.setVisibility(View.VISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true; // không xử lý khi đang gõ
            }
        });
    }

    private void searchUsers(String ho_ten) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("ho_ten", ho_ten); // truyền từ khoá tìm kiếm

                // Gọi API POST
                JSONObject response = ApiClient.post("search_user.php", requestData);
                if (response != null && response.optBoolean("success", false)) {
                    ArrayList<Object> tempList = new ArrayList<>();
                    JSONArray usersArray = response.getJSONArray("user");

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);

                        User user_finded = new User();
                        user_finded.setNguoi_dung_id(user.getInt("nguoi_dung_id"));
                        user_finded.setHo_ten(user.getString("ho_ten"));
                        user_finded.setAvatar(user.getString("url_anh_dai_dien"));

                        tempList.add(user_finded);

                    }

                    requireActivity().runOnUiThread(() -> {

                        results.clear();
                        results.addAll(tempList);
                        Log.d(TAG, "Số lượng kết quả: " + tempList.size());
                        adapter = new MultiTypeAdapter(getContext(),results,"friend_list",this);
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();


                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                Log.e("SearchFragment", "Lỗi tìm kiếm", e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi tìm kiếm", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    public void onAddFriendAction(int friendID) {
        Log.d(TAG,"taikhoan id:"+taiKhoanId);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("nguoi_dung_id", taiKhoanId);
                requestData.put("banbeid", friendID);

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
}
