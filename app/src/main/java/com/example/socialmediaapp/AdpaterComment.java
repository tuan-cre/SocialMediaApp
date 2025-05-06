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

public class AdpaterComment extends ArrayAdapter<CommentItem> {
    UpLoadImg uploadImg = new UpLoadImg(this.getContext());
    ArrayList<CommentItem> listCommentItem = null;

    public AdpaterComment(@NonNull Context context, int resource, @NonNull ArrayList<CommentItem> objects) {
        super(context, resource, objects);
        listCommentItem = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view= inflater.inflate(R.layout.item_comment, null);

        CommentItem commentItem = listCommentItem.get(position);
        TextView txtDate_CMT= view.findViewById(R.id.txtDate_CMT);
        TextView txtName_CMT = view.findViewById(R.id.txtName_CMT);
        TextView txtContent_CMT = view.findViewById(R.id.txtContent_CMT);
        ImageView imgAvatar_CMT = view.findViewById(R.id.imgAvatar_CMT);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (commentItem.getDateCreated() != null) {
            txtDate_CMT.setText(sdf.format(commentItem.getDateCreated()));
        } else {
            txtDate_CMT.setText("Chưa có ngày");
        }
        txtName_CMT.setText(commentItem.getCommenterName());
        txtContent_CMT.setText(commentItem.getCommentContent());
        uploadImg.setImageToView(commentItem.getUrlAvatar(), imgAvatar_CMT);

        return  view;
    }
}
