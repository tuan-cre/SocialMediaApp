package com.example.socialmediaapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentItem {
    private int commentId;
    private String commenterName;
    private String urlAvatar;
    private String commentContent;
    private Date dateCreated;

    public CommentItem(int commentId, String commenterName, String commentContent, String urlAvatar, String dateCreatedStr) {
        this.commentId = commentId;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.urlAvatar = urlAvatar;
        try {
            // Parse string into Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            this.dateCreated = sdf.parse(dateCreatedStr);
        } catch (ParseException e) {
            e.printStackTrace();
            this.dateCreated = null;
        }
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    // Optional: Get formatted date string
    public String getFormattedDate() {
        if (dateCreated == null) return "";
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        return outputFormat.format(dateCreated);
    }
}
