package com.example.socialmediaapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostItem {
    private String noiDungBaiViet;
    private String nguoiDung;
    private String urlAvatar;
    private Date ngayBaiViet;
    private int id;
    private int idNhom;

    public PostItem(String noiDung, String nguoiDung, String urlAvatar, String ngayBaiVietStr, int id, int idNhom) {
        this.noiDungBaiViet = noiDung;
        this.nguoiDung = nguoiDung;
        this.urlAvatar = urlAvatar;

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
}
