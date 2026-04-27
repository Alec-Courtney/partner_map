package com.androidcourse.partner_map.model;

import java.util.List;

public class PartnerRequest {
    private String requestId;
    private String publisherId;
    private String publisherName;
    private String publisherAvatar;
    private String publisherSchool;
    private String title;
    private String description;
    private int category;
    private double requestLat;
    private double requestLng;
    private double publishLat;
    private double publishLng;
    private int maxParticipants;
    private int currentParticipants;
    private long scheduledTime;
    private int expireBeforeMin;
    private int genderRequirement; // 0=不限, 1=仅男, 2=仅女
    private String costDescription;
    private int status; // 0=招募中, 1=已满员, 2=已结束
    private long createdAt;
    private boolean isSnapshot;
    private List<User> participants;

    public PartnerRequest() {}

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPublisherId() { return publisherId; }
    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getPublisherAvatar() { return publisherAvatar; }
    public void setPublisherAvatar(String publisherAvatar) { this.publisherAvatar = publisherAvatar; }

    public String getPublisherSchool() { return publisherSchool; }
    public void setPublisherSchool(String publisherSchool) { this.publisherSchool = publisherSchool; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategory() { return category; }
    public void setCategory(int category) { this.category = category; }

    public double getRequestLat() { return requestLat; }
    public void setRequestLat(double requestLat) { this.requestLat = requestLat; }

    public double getRequestLng() { return requestLng; }
    public void setRequestLng(double requestLng) { this.requestLng = requestLng; }

    public double getPublishLat() { return publishLat; }
    public void setPublishLat(double publishLat) { this.publishLat = publishLat; }

    public double getPublishLng() { return publishLng; }
    public void setPublishLng(double publishLng) { this.publishLng = publishLng; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }

    public long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }

    public int getExpireBeforeMin() { return expireBeforeMin; }
    public void setExpireBeforeMin(int expireBeforeMin) { this.expireBeforeMin = expireBeforeMin; }

    public int getGenderRequirement() { return genderRequirement; }
    public void setGenderRequirement(int genderRequirement) { this.genderRequirement = genderRequirement; }

    public String getCostDescription() { return costDescription; }
    public void setCostDescription(String costDescription) { this.costDescription = costDescription; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isSnapshot() { return isSnapshot; }
    public void setSnapshot(boolean snapshot) { isSnapshot = snapshot; }

    public List<User> getParticipants() { return participants; }
    public void setParticipants(List<User> participants) { this.participants = participants; }
}
