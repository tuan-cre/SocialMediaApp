package com.example.socialmediaapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostItem {
    private String noiDungBaiViet;
    private String nguoiDung;
    private String urlAvatar;

    private String urlPost;
    private Date ngayBaiViet;
    private int id;
    private int idNhom;
    private Boolean isComment = false;
    private ArrayList<CommentItem> commentList;
    private AdpaterComment commentAdapter;

    public PostItem(String noiDung, String nguoiDung, String urlAvatar, String ngayBaiVietStr, String urlPost, int id, int idNhom) {
        this.noiDungBaiViet = noiDung;
        this.nguoiDung = nguoiDung;
        this.urlAvatar = urlAvatar;
        this.urlPost = urlPost;

        try {
            // Format ngày
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.ngayBaiViet = sdf.parse(ngayBaiVietStr);
        } catch (ParseException e) {
            this.ngayBaiViet = null;
            e.printStackTrace();
        }

        this.id = id;
        this.idNhom = idNhom;
    }

    public String getNoiDungBaiViet() {
        return noiDungBaiViet;
    }

    public String getNguoiDung() {
        return nguoiDung;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public Date getNgayBaiViet() {
        return ngayBaiViet;
    }

    public int getId() {
        return id;
    }

    public int getIdNhom() {
        return idNhom;
    }

    public String getUrlPost() {
        return urlPost;
    }

    public void setUrlPost(String urlPost) {
        this.urlPost = urlPost;
    }

    public Boolean getIsComment() {
        return isComment;
    }

    public void setIsComment(Boolean comment) {
        isComment = comment;
    }

    public void setCommentList(ArrayList<CommentItem> comments) {
        this.commentList = comments;
    }

    public ArrayList<CommentItem> getCommentList() {
        return commentList;
    }

    public void setCommentAdapter(AdpaterComment adapter) {
        this.commentAdapter = adapter;
    }

    public AdpaterComment getCommentAdapter() {
        return commentAdapter;
    }
}
