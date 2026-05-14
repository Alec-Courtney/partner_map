package com.androidcourse.partner_map.model;

import com.google.gson.annotations.SerializedName;

public class ChatRoom {
    private String chatRoomId;
    private String requestId;
    private String requestTitle;
    private String requesterId;
    @SerializedName(value = "requesterNickname", alternate = {"requesterName"})
    private String requesterName;
    private String requesterAvatar;
    private String publisherId;
    @SerializedName(value = "publisherNickname", alternate = {"publisherName"})
    private String publisherName;
    private String publisherAvatar;
    private String lastMessage;
    private Long lastMessageAt;
    private Integer status;

    public ChatRoom() {}

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequestTitle() {
        return requestTitle == null ? "" : requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterAvatar() { return requesterAvatar; }
    public void setRequesterAvatar(String requesterAvatar) { this.requesterAvatar = requesterAvatar; }

    public String getPublisherId() { return publisherId; }
    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getPublisherAvatar() { return publisherAvatar; }
    public void setPublisherAvatar(String publisherAvatar) { this.publisherAvatar = publisherAvatar; }

    public String getLastMessage() {
        return lastMessage == null ? "" : lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageAt() {
        return lastMessageAt == null ? 0L : lastMessageAt;
    }

    public void setLastMessageAt(Long lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public int getStatus() { return status == null ? 0 : status; }
    public void setStatus(Integer status) { this.status = status; }
}
