package com.androidcourse.partner_map.model;

import com.google.gson.annotations.SerializedName;

public class Participation {
    private String participationId;
    private String requestId;
    private String requestTitle;
    private String userId;
    @SerializedName(value = "userName", alternate = {"nickname"})
    private String userName;
    @SerializedName(value = "userAvatar", alternate = {"avatar"})
    private String userAvatar;
    @SerializedName(value = "userSchool", alternate = {"schoolName"})
    private String userSchool;
    private Integer status;
    @SerializedName(value = "createdAt", alternate = {"joinedAt"})
    private Long createdAt;

    public Participation() {
    }

    public String getParticipationId() {
        return participationId;
    }

    public void setParticipationId(String participationId) {
        this.participationId = participationId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTitle() {
        return requestTitle == null ? "" : requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserSchool() {
        return userSchool == null ? "" : userSchool;
    }

    public void setUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public int getStatus() {
        return status == null ? 0 : status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt == null ? 0L : createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
