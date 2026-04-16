package com.androidcourse.partner_map.model;

public class Participation {
    private String participationId;
    private String requestId;
    private String requestTitle;
    private String userId;
    private String userName;
    private String userAvatar;
    private String userSchool;
    private int status; // 0=待审批, 1=已加入, 2=已退出
    private long createdAt;

    public Participation() {}

    public String getParticipationId() { return participationId; }
    public void setParticipationId(String participationId) { this.participationId = participationId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequestTitle() { return requestTitle; }
    public void setRequestTitle(String requestTitle) { this.requestTitle = requestTitle; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getUserSchool() { return userSchool; }
    public void setUserSchool(String userSchool) { this.userSchool = userSchool; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
