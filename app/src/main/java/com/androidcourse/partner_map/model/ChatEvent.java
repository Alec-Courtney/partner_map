package com.androidcourse.partner_map.model;

public class ChatEvent {
    private final String type;
    private final String chatRoomId;
    private final String requestId;

    public ChatEvent(String type, String chatRoomId, String requestId) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.requestId = requestId;
    }

    public String getType() {
        return type;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public String getRequestId() {
        return requestId;
    }
}
