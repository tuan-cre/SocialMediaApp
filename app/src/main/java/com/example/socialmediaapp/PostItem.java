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
    private Boolean isLike = false;

    private int soLuongLike;
    private ArrayList<CommentItem> commentList;
    private AdapterComment commentAdapter;

    public PostItem(String noiDung, String nguoiDung, String urlAvatar, String ngayBaiVietStr, String urlPost, int id, int idNhom, int soLuongLike) {
        this.noiDungBaiViet = noiDung;
        this.nguoiDung = nguoiDung;
        this.urlAvatar = urlAvatar;
        this.urlPost = urlPost;
        this.soLuongLike = soLuongLike;

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

    public void setCommentAdapter(AdapterComment adapter) {
        this.commentAdapter = adapter;
    }

    public AdapterComment getCommentAdapter() {
        return commentAdapter;
    }

    public Boolean getIsLike() { return isLike; }

    public void setIsLike(Boolean like) { isLike = like; }

    public int getSoLuongLike() { return soLuongLike; }

    public void setSoLuongLike(int soLuongLike) { this.soLuongLike = soLuongLike; }
}
