package com.example.socialmediaapp;

public class User {
    private int nguoi_dung_id;
    private  String ho_ten;
    private String avatar;
    private  String gioi_thieu;

    public User() {
    }

    public User(int nguoi_dung_id, String ho_ten, String avatar) {
        this.nguoi_dung_id = nguoi_dung_id;
        this.ho_ten = ho_ten;
        this.avatar = avatar;
    }

    private String ngay_sinh;
    private String gioi_tinh;
    private String que_quan;
    private String ngay_tao;

    public int getNguoi_dung_id() {
        return nguoi_dung_id;
    }

    public void setNguoi_dung_id(int nguoi_dung_id) {
        this.nguoi_dung_id = nguoi_dung_id;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGioi_thieu() {
        return gioi_thieu;
    }

    public void setGioi_thieu(String gioi_thieu) {
        this.gioi_thieu = gioi_thieu;
    }

    public String getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(String ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public String getQue_quan() {
        return que_quan;
    }

    public void setQue_quan(String que_quan) {
        this.que_quan = que_quan;
    }

    public String getNgay_tao() {
        return ngay_tao;
    }

    public void setNgay_tao(String ngay_tao) {
        this.ngay_tao = ngay_tao;
    }
}
