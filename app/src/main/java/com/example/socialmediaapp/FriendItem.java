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

    private int nguoi_dung_id;
    private int ban_be_id;
    private String trang_thai;
    private Date ngay_tao;
}
