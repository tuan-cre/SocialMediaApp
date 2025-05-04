package com.example.socialmediaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MultiTypeAdapter extends ArrayAdapter<Object> {
    private final LayoutInflater inflater;
    private  final UpLoadImg uploadImg = new UpLoadImg(getContext());

    public MultiTypeAdapter(Context context, ArrayList<Object> objects) {
        super(context, 0, objects);
        inflater = LayoutInflater.from(context);
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
            view = inflater.inflate(R.layout.friend_list_item, parent, false);

            FriendItem friendItem = (FriendItem) item;
//            TextView txtName = view.findViewById(R.id.txtName_Friend);
//            ImageView imgAvatar = view.findViewById(R.id.imgAvatar_Friend);
            TextView txtFriendId = view.findViewById(R.id.txtfriendId);
            TextView txtTrangThai = view.findViewById(R.id.txtTrangThai);
            txtFriendId.setText(friendItem.getBan_be_id() + "");
            txtTrangThai.setText(friendItem.getTrang_thai());

//            txtName.setText(friendItem.getName());
            // uploadImg.setImageToView(friendItem.getAvatarUrl(), imgAvatar);

        } else {
            view = new View(getContext()); // fallback
        }

        return view;
    }
}
