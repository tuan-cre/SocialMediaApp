package com.example.socialmediaapp;

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

public class AdapterPost extends ArrayAdapter<PostItem> {
    UpLoadImg uploadImg = new UpLoadImg(this.getContext());
    ArrayList<PostItem> listPostItem = null;
    public AdapterPost(Bai_Dang context, int resource, ArrayList<PostItem> objects) {
        super(context, resource, objects);
        listPostItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = null;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        v = inflater.inflate(R.layout.activity_post_list_view_item, null);

        PostItem postItem = listPostItem.get(position);
        TextView txtDate_Post = v.findViewById(R.id.txtDate_PostItem);
        TextView txtNguoiDung = v.findViewById(R.id.txtName_PostItem);
        TextView txtNoiDung_Post = v.findViewById(R.id.txtNoiDung_PostIem);
        ImageView imgAvatar = v.findViewById(R.id.imgAvatar_PostItem);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (postItem.getNgayBaiViet() != null) {
            txtDate_Post.setText(sdf.format(postItem.getNgayBaiViet()));
        } else {
            txtDate_Post.setText("Chưa có ngày");
        }

        txtNguoiDung.setText(postItem.getNguoiDung().toString());
        txtNoiDung_Post.setText(postItem.getNoiDungBaiViet().toString());
        //uploadImg.setImageToView(postItem.getUrlAvatar().toString(), imgAvatar);
        return v;
    }
}
