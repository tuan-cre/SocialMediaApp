package com.example.socialmediaapp;

public class Message {
    private int nguoi_gui_id;
    private int nguoi_nhan_id;
    private String Content;
    private String ngay_gui;
    private boolean trang_thai_doc;

    public boolean isChu_tin_nhan() {
        return chu_tin_nhan;
    }

    public void setChu_tin_nhan(boolean chu_tin_nhan) {
        this.chu_tin_nhan = chu_tin_nhan;
    }

    private boolean chu_tin_nhan;

    public Message(int nguoi_gui_id, int nguoi_nhan_id, String content, String ngay_gui, boolean trang_thai_doc) {
        this.nguoi_gui_id = nguoi_gui_id;
        this.nguoi_nhan_id = nguoi_nhan_id;
        Content = content;
        this.ngay_gui = ngay_gui;
        this.trang_thai_doc = trang_thai_doc;
    }

    public Message(String content, boolean chu_tin_nhan) {
        Content = content;
        this.chu_tin_nhan = chu_tin_nhan;
    }

    public int getNguoi_gui_id() {
        return nguoi_gui_id;
    }

    public void setNguoi_gui_id(int nguoi_gui_id) {
        this.nguoi_gui_id = nguoi_gui_id;
    }

    public int getNguoi_nhan_id() {
        return nguoi_nhan_id;
    }

    public void setNguoi_nhan_id(int nguoi_nhan_id) {
        this.nguoi_nhan_id = nguoi_nhan_id;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getNgay_gui() {
        return ngay_gui;
    }

    public void setNgay_gui(String ngay_gui) {
        this.ngay_gui = ngay_gui;
    }

    public boolean isTrang_thai_doc() {
        return trang_thai_doc;
    }

    public void setTrang_thai_doc(boolean trang_thai_doc) {
        this.trang_thai_doc = trang_thai_doc;
    }
}
