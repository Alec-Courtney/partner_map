package com.androidcourse.partner_map.model;

public class User {
    private String userId;
    private String nickname;
    private String avatar;
    private String schoolId;
    private String schoolName;
    private String gender;
    private float attendRate;
    private float praiseRate;
    private String token;

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public float getAttendRate() { return attendRate; }
    public void setAttendRate(float attendRate) { this.attendRate = attendRate; }

    public float getPraiseRate() { return praiseRate; }
    public void setPraiseRate(float praiseRate) { this.praiseRate = praiseRate; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
