package com.example.socialmediaapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiTypeAdapter extends ArrayAdapter<Object> {
    private final LayoutInflater inflater;
    private final UpLoadImg uploadImg;
    private final String mode;
    private ArrayList<CommentItem> listCommentItem = null;
    private final OnFriendActionListener friendActionListener;
    private final OnAddFriendListener addFriendListener;

    public MultiTypeAdapter(Context context, ArrayList<Object> objects, String mode) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
        this.uploadImg = new UpLoadImg(context);
        this.mode = mode;
        this.friendActionListener = null;
        this.addFriendListener =null;
    }

    public MultiTypeAdapter(Context context, ArrayList<Object> objects, String mode, OnFriendActionListener listener) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
        this.uploadImg = new UpLoadImg(context);
        this.mode = mode;
        this.friendActionListener = listener;
        this.addFriendListener = null;
    }
    public MultiTypeAdapter(Context context, ArrayList<Object> objects, String mode, OnAddFriendListener addListener) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
        this.uploadImg = new UpLoadImg(context);
        this.mode = mode;
        this.friendActionListener = null;
        this.addFriendListener = addListener;
    }

    public interface OnFriendActionListener {
        void onFriendAction(FriendItem item, String action, int friendID);
    }

    public interface OnAddFriendListener {
        void onAddFriendAction(int friendID); // "accepted" or "denied"
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Object item = getItem(position);
        View view;
        SharedPreferences prefs = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int nguoi_dung_id = prefs.getInt("tai_khoan_id", -1);

        if (item instanceof PostItem) {
            view = inflater.inflate(R.layout.activity_post_list_view_item, parent, false);
            PostItem postItem = (PostItem) item;

            TextView txtDate_Post = view.findViewById(R.id.txtDate_PostItem);
            TextView txtNguoiDung = view.findViewById(R.id.txtName_PostItem);
            TextView txtNoiDung_Post = view.findViewById(R.id.txtNoiDung_PostIem);
            ImageView imgAvatar = view.findViewById(R.id.imgAvatar_PostItem);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtDate_Post.setText(postItem.getNgayBaiViet() != null ? sdf.format(postItem.getNgayBaiViet()) : "Chưa có ngày");
            txtNguoiDung.setText(postItem.getNguoiDung());
            txtNoiDung_Post.setText(postItem.getNoiDungBaiViet());
            uploadImg.setImageToView(postItem.getUrlAvatar(), imgAvatar);
            ImageView imgPost = view.findViewById(R.id.imgPost_Home);

            if (!"null".equals(postItem.getUrlPost()) && !postItem.getUrlPost().isEmpty()) {
                imgPost.setVisibility(View.VISIBLE);
                uploadImg.setImageToView(postItem.getUrlPost(), imgPost);
            } else {
                imgPost.setVisibility(View.GONE);
            }

            Button btnLike_Home = view.findViewById(R.id.btnLike_Home);
            Button btnComment_Home = view.findViewById(R.id.btnComment_Home);
            ListView lvComment = view.findViewById(R.id.lvComment);
            LinearLayout llcomment = view.findViewById(R.id.lloComment);
            TextView txtComment = view.findViewById(R.id.txtComment_Home);
            Button btnSendComment = view.findViewById(R.id.btnSendComment);

            llcomment.setVisibility(postItem.getIsComment() ? View.VISIBLE : View.GONE);

            btnComment_Home.setOnClickListener(v -> {
                boolean newState = !postItem.getIsComment();
                postItem.setIsComment(newState);
                llcomment.setVisibility(postItem.getIsComment() ? View.VISIBLE : View.GONE);
                Log.d("MultiTypeAdapter", "Comment button clicked, newState: " + newState);
                if (newState) {
                    GetDSComment(postItem.getId(), comments -> {
                        Log.d("MultiTypeAdapter", "Comments received: " + comments.size());
                        if (comments != null && !comments.isEmpty()) {
                            postItem.setCommentList(comments);
                            AdapterComment adapter = new AdapterComment(getContext(), R.layout.item_comment, comments);
                            postItem.setCommentAdapter(adapter);
                            lvComment.setAdapter(adapter);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No comments yet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                notifyDataSetChanged();
            });

            if (postItem.getIsComment()) {
                ArrayList<CommentItem> comments = postItem.getCommentList();
                AdapterComment adapter = postItem.getCommentAdapter();
                if (comments != null && adapter != null) {
                    lvComment.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            btnSendComment.setOnClickListener(v1 -> {
                String noidung = txtComment.getText().toString();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        JSONObject requestData = new JSONObject();
                        requestData.put("bai_viet_id", postItem.getId());
                        requestData.put("noi_dung", noidung);
                        requestData.put("nguoi_dung_id", nguoi_dung_id);

                        JSONObject response = ApiClient.post("comment.php", requestData);

                        ((Activity) getContext()).runOnUiThread(() -> {
                            try {
                                if (response != null && response.getBoolean("success")) {
                                    txtComment.setText("");
                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                    GetDSComment(postItem.getId(), comments -> {
                                        postItem.setCommentList(comments);
                                        AdapterComment newAdapter = new AdapterComment(getContext(), R.layout.item_comment, comments);
                                        postItem.setCommentAdapter(newAdapter);
                                        lvComment.setAdapter(newAdapter);
                                        newAdapter.notifyDataSetChanged();
                                    });
                                } else {
                                    Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        executor.shutdown();
                    }
                });
            });

        } else if (item instanceof FriendItem) {
            FriendItem friendItem = (FriendItem) item;
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

            TextView txtFriendId = view.findViewById(R.id.txtfriendId);
            TextView txtTrangThai = view.findViewById(R.id.txtTrangThai);
            Button btnAccept = view.findViewById(R.id.btnAccept);
            Button btnDecline = view.findViewById(R.id.btnDecline);
            ImageView imgAnhBanBe = view.findViewById(R.id.imgAnhBanBe);

            uploadImg.setImageToView(friendItem.getUrlAvatar(), imgAnhBanBe);

            if ("Invite".equals(mode)) {
                txtFriendId.setText(String.valueOf(friendItem.getTen_ban_be()));
                txtTrangThai.setText(friendItem.getTrang_thai());
                txtTrangThai.setVisibility(View.INVISIBLE);
                btnAccept.setVisibility(View.VISIBLE);
                btnDecline.setVisibility(View.VISIBLE);
                Log.d("MultiTypeAdapter", "Invite mode, friendItem: " + friendItem.toString());

                btnAccept.setOnClickListener(v -> {
                    if (friendActionListener != null) {
                        friendActionListener.onFriendAction(friendItem, "đồng ý", 0);
                    }
                });

                btnDecline.setOnClickListener(v -> {
                    if (friendActionListener != null) {
                        friendActionListener.onFriendAction(friendItem, "từ chối", 0);
                    }
                });

            } else {
                txtFriendId.setText(friendItem.getTen_ban_be());
                txtTrangThai.setText("Bạn bè");

                btnAccept.setVisibility(View.GONE);
                btnDecline.setVisibility(View.GONE);
            }

        } else if (item instanceof User ) {
            view = inflater.inflate(R.layout.item_user, parent, false);

            User user = (User) item;

            TextView txtName = view.findViewById(R.id.txtUserName);
            ImageView imgAvatar = view.findViewById(R.id.imgUserAvatar);
            Button btnAddFriend = view.findViewById(R.id.btnAddFriend);
            Log.d("MutiTypeAdapter","id_nguoi_dung: "+user.getNguoi_dung_id());

            txtName.setText(user.getHo_ten());
            uploadImg.setImageToView(user.getAvatar(), imgAvatar);
            btnAddFriend.setOnClickListener(v -> {
//                addFriendListener.onAddFriendAction(user.getNguoi_dung_id());
                friendActionListener.onFriendAction(null , "them", user.getNguoi_dung_id());
            });
        }

        else {
            view = new View(getContext()); // fallback
        }

        return view;
    }

    public interface OnCommentsLoadedListener {
        void onCommentsLoaded(ArrayList<CommentItem> comments);
    }

    public ArrayList<CommentItem> GetDSComment(int idBaiViet, OnCommentsLoadedListener listener) {
        listCommentItem = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                JSONObject requestData = new JSONObject();
                requestData.put("bai_viet_id", idBaiViet);

                JSONObject response = ApiClient.post("load_comment.php", requestData);

                if (response != null && response.getBoolean("success") && response.has("comments")) {
                    for (int i = 0; i < response.getJSONArray("comments").length(); i++) {
                        JSONObject comment = response.getJSONArray("comments").getJSONObject(i);

                        int binh_luan_id = comment.optInt("binh_luan_id", -1);
                        String ho_ten = comment.optString("ho_ten", "");
                        String noi_dung = comment.optString("noi_dung", "");
                        String urlAvatar = comment.optString("url_anh_dai_dien", "");
                        String ngay_tao = comment.optString("ngay_tao", "");

                        CommentItem commentItem = new CommentItem(binh_luan_id, ho_ten, noi_dung, urlAvatar, ngay_tao);
                        listCommentItem.add(commentItem);
                    }
                }

                ((Activity) getContext()).runOnUiThread(() -> listener.onCommentsLoaded(listCommentItem));
            } catch (Exception e) {
                Log.e("MultiTypeAdapter", "Lỗi khi load comment", e);
            } finally {
                executor.shutdown();
            }
        });

        return listCommentItem;
    }
}
