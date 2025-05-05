package com.example.socialmediaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MultiTypeAdapter extends ArrayAdapter<Object> {
    private final LayoutInflater inflater;
    private final UpLoadImg uploadImg;
    private final String mode; // "friend_list" or "friend_invite"
    private final OnFriendActionListener friendActionListener;
    public MultiTypeAdapter(Context context, ArrayList<Object> objects, String mode) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
        this.uploadImg = new UpLoadImg(context);
        this.mode = mode;
        this.friendActionListener = null;
    }

    public MultiTypeAdapter(Context context, ArrayList<Object> objects, String mode, OnFriendActionListener listener) {
        super(context, 0, objects);
        this.inflater = LayoutInflater.from(context);
        this.uploadImg = new UpLoadImg(context);
        this.mode = mode;
        this.friendActionListener = listener;
    }

    public interface OnFriendActionListener {
        void onFriendAction(FriendItem item, String action); // "accepted" or "denied"
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Object item = getItem(position);
        View view;

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

        } else if (item instanceof FriendItem) {
            FriendItem friendItem = (FriendItem) item;

            if ("Invite".equals(mode)) {
                view = inflater.inflate(R.layout.friend_list_item, parent, false);

                TextView txtFriendId = view.findViewById(R.id.txtfriendId);
                TextView txtTrangThai = view.findViewById(R.id.txtTrangThai);
                Button btnAccept = view.findViewById(R.id.btnAccept);
                Button btnDecline = view.findViewById(R.id.btnDecline);

                txtFriendId.setText(String.valueOf(friendItem.getBan_be_id()));
                txtTrangThai.setText(friendItem.getTrang_thai());

                btnAccept.setOnClickListener(v -> {
                    if (friendActionListener != null) {
                        friendActionListener.onFriendAction(friendItem, "đồng ý");
                    }
                });

                btnDecline.setOnClickListener(v -> {
                    if (friendActionListener != null) {
                        friendActionListener.onFriendAction(friendItem, "từ chối");
                    }
                });

            } else { // "friend_list"
                view = inflater.inflate(R.layout.friend_list_item, parent, false);

                TextView txtFriendId = view.findViewById(R.id.txtfriendId);
                TextView txtTrangThai = view.findViewById(R.id.txtTrangThai);
                Button btnAccept = view.findViewById(R.id.btnAccept);
                Button btnDecline = view.findViewById(R.id.btnDecline);
                ImageView imgAnhBanBe = view.findViewById(R.id.imgAnhBanBe);

                uploadImg.setImageToView(friendItem.getUrlAvatar(), imgAnhBanBe);

                btnAccept.setVisibility(view.GONE);;
                btnDecline.setVisibility(view.GONE);;

//                txtFriendId.setText(String.valueOf(friendItem.getBan_be_id()));
//                txtTrangThai.setText(friendItem.getTrang_thai()); // or "Bạn bè"
                txtFriendId.setText(friendItem.getTen_ban_be());
                txtTrangThai.setText("Bạn bè");
            }

        } else {
            view = new View(getContext()); // fallback for unknown type
        }

        return view;
    }
}
