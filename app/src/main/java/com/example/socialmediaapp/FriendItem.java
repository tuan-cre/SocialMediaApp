package com.example.socialmediaapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FriendItem {
    public int getNguoi_dung_id() {
        return nguoi_dung_id;
    }

    public void setNguoi_dung_id(int nguoi_dung_id) {
        this.nguoi_dung_id = nguoi_dung_id;
    }

    public int getBan_be_id() {
        return ban_be_id;
    }

    public void setBan_be_id(int ban_be_id) {
        this.ban_be_id = ban_be_id;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public Date getNgay_tao() {
        return ngay_tao;
    }

    public void setNgay_tao(Date ngay_tao) {
        this.ngay_tao = ngay_tao;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }
    public String getTen_ban_be() {
        return ten_ban_be;
    }

    public void setTen_ban_be(String ten_ban_be) {
        this.ten_ban_be = ten_ban_be;
    }

    public FriendItem(int nguoi_dung_id, int ban_be_id, String trang_thai, String ngaytao) {
        this.nguoi_dung_id = nguoi_dung_id;
        this.ban_be_id = ban_be_id;
        this.trang_thai = trang_thai;

        try {
            // Format ngày
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.ngay_tao = sdf.parse(ngaytao);
        } catch (ParseException e) {
            this.ngay_tao = null;
            e.printStackTrace();
        }
        this.ngay_tao = ngay_tao;
    }

    public FriendItem(int nguoi_dung_id, int ban_be_id, String urlAvatar, String trang_thai, String ngaytao, String ten_ban_be) {
        this.nguoi_dung_id = nguoi_dung_id;
        this.ban_be_id = ban_be_id;
        this.urlAvatar = urlAvatar;
        this.trang_thai = trang_thai;
        try {
            // Format ngày
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.ngay_tao = sdf.parse(ngaytao);
        } catch (ParseException e) {
            this.ngay_tao = null;
            e.printStackTrace();
        }
        this.ngay_tao = ngay_tao;
        this.ten_ban_be = ten_ban_be;
    }

    private int nguoi_dung_id;
    private int ban_be_id;
    private String urlAvatar;
    private String trang_thai;
    private Date ngay_tao;
    private String ten_ban_be;
}
