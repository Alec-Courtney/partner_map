package com.androidcourse.partner_map.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PartnerRequest {
    private String requestId;
    private String publisherId;
    @SerializedName(value = "publisherNickname", alternate = {"publisherName"})
    private String publisherNickname;
    private String publisherAvatar;
    private String title;
    private String description;
    private Integer category;
    private String categoryName;
    private Double requestLat;
    private Double requestLng;
    private String requestAddress;
    private Double publishLat;
    private Double publishLng;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Long scheduledTime;
    private Integer expireBeforeMin;
    private Integer genderRequirement;
    private String genderRequirementName;
    private String costDescription;
    private Integer status;
    private String statusName;
    private Long createdAt;
    private Boolean isPublisher;
    private Integer myParticipationStatus;
    private String mySnapshotData;
    private List<User> participants;
    private transient float distanceMeters;

    public PartnerRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherNickname == null ? "" : publisherNickname;
    }

    public void setPublisherName(String publisherNickname) {
        this.publisherNickname = publisherNickname;
    }

    public String getPublisherAvatar() {
        return publisherAvatar;
    }

    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category == null ? 0 : category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName == null ? "" : categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getRequestLat() {
        return requestLat == null ? 0D : requestLat;
    }

    public void setRequestLat(Double requestLat) {
        this.requestLat = requestLat;
    }

    public double getRequestLng() {
        return requestLng == null ? 0D : requestLng;
    }

    public void setRequestLng(Double requestLng) {
        this.requestLng = requestLng;
    }

    public String getRequestAddress() {
        return requestAddress == null ? "" : requestAddress;
    }

    public void setRequestAddress(String requestAddress) {
        this.requestAddress = requestAddress;
    }

    public double getPublishLat() {
        return publishLat == null ? 0D : publishLat;
    }

    public void setPublishLat(Double publishLat) {
        this.publishLat = publishLat;
    }

    public double getPublishLng() {
        return publishLng == null ? 0D : publishLng;
    }

    public void setPublishLng(Double publishLng) {
        this.publishLng = publishLng;
    }

    public int getMaxParticipants() {
        return maxParticipants == null ? 0 : maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants == null ? 0 : currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public long getScheduledTime() {
        return scheduledTime == null ? 0L : scheduledTime;
    }

    public void setScheduledTime(Long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getExpireBeforeMin() {
        return expireBeforeMin == null ? 0 : expireBeforeMin;
    }

    public void setExpireBeforeMin(Integer expireBeforeMin) {
        this.expireBeforeMin = expireBeforeMin;
    }

    public int getGenderRequirement() {
        return genderRequirement == null ? 0 : genderRequirement;
    }

    public void setGenderRequirement(Integer genderRequirement) {
        this.genderRequirement = genderRequirement;
    }

    public String getGenderRequirementName() {
        return genderRequirementName == null ? "" : genderRequirementName;
    }

    public void setGenderRequirementName(String genderRequirementName) {
        this.genderRequirementName = genderRequirementName;
    }

    public String getCostDescription() {
        return costDescription == null ? "" : costDescription;
    }

    public void setCostDescription(String costDescription) {
        this.costDescription = costDescription;
    }

    public int getStatus() {
        return status == null ? 0 : status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName == null ? "" : statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public long getCreatedAt() {
        return createdAt == null ? 0L : createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPublisher() {
        return isPublisher != null && isPublisher;
    }

    public void setPublisher(Boolean publisher) {
        isPublisher = publisher;
    }

    public Integer getMyParticipationStatus() {
        return myParticipationStatus;
    }

    public void setMyParticipationStatus(Integer myParticipationStatus) {
        this.myParticipationStatus = myParticipationStatus;
    }

    public String getMySnapshotData() {
        return mySnapshotData;
    }

    public void setMySnapshotData(String mySnapshotData) {
        this.mySnapshotData = mySnapshotData;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public float getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(float distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
