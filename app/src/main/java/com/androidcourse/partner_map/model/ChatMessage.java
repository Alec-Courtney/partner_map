package com.androidcourse.partner_map.model;

public class ChatMessage {
    private String messageId;
    private String chatRoomId;
    private String senderId;
    private String senderName;
    private String content;
    private long timestamp;
    private boolean isSent;

    public ChatMessage() {}

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getChatRoomId() { return chatRoomId; }
    public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isSent() { return isSent; }
    public void setSent(boolean sent) { isSent = sent; }
}
