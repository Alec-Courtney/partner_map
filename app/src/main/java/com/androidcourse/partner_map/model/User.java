package com.androidcourse.partner_map.model;

public class User {
    private String userId;
    private String nickname;
    private String avatar;
    private String schoolId;
    private String schoolName;
    private Integer gender;
    private String genderName;
    private Double attendRate;
    private Double praiseRate;
    private Integer publishCount;
    private Integer participateCount;
    private String token;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname == null ? "" : nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName == null ? "" : schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getGender() {
        return gender == null ? 0 : gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getGenderName() {
        return genderName == null ? "" : genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public double getAttendRate() {
        return attendRate == null ? 0D : attendRate;
    }

    public void setAttendRate(Double attendRate) {
        this.attendRate = attendRate;
    }

    public double getPraiseRate() {
        return praiseRate == null ? 0D : praiseRate;
    }

    public void setPraiseRate(Double praiseRate) {
        this.praiseRate = praiseRate;
    }

    public int getPublishCount() {
        return publishCount == null ? 0 : publishCount;
    }

    public void setPublishCount(Integer publishCount) {
        this.publishCount = publishCount;
    }

    public int getParticipateCount() {
        return participateCount == null ? 0 : participateCount;
    }

    public void setParticipateCount(Integer participateCount) {
        this.participateCount = participateCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
