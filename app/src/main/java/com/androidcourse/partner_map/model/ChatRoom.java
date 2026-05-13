package com.androidcourse.partner_map.model;

import com.google.gson.annotations.SerializedName;

public class ChatRoom {
    private String chatRoomId;
    private String requestId;
    private String requesterId;
    @SerializedName("requesterNickname")
    private String requesterName;
    private String requesterAvatar;
    private String publisherId;
    @SerializedName("publisherNickname")
    private String publisherName;
    private String publisherAvatar;
    private int status; // 0=进行中, 1=已解散

    public ChatRoom() {}

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

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

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
