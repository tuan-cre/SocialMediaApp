package com.example.socialmediaapp;

public class CommentItem {
    private int commentId;
    private String commenterName;

    private String urlAvatar;
    private String commentContent;
    private String dateCreated;

    public CommentItem(int commentId, String commenterName, String commentContent, String urlAvatar, String dateCreated) {
        this.commentId = commentId;
        this.commenterName = commenterName;
        this.commentContent = commentContent;
        this.urlAvatar = urlAvatar;
        this.dateCreated = dateCreated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
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
}
