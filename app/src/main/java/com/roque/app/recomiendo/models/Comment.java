package com.roque.app.recomiendo.models;

public class Comment {

    private String comment;
    private String userId;
    private String siteId;

    public Comment() {
    }

    public Comment(String comment, String userId, String siteId) {
        this.comment = comment;
        this.userId = userId;
        this.siteId = siteId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
