package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.data.repository.ChatRepository;
import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.ChatEvent;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.model.PartnerRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private final RequestRepository requestRepository;
    private final MutableLiveData<ChatMessage> newMessage = new MutableLiveData<>();
    private final MutableLiveData<ChatEvent> chatEvent = new MutableLiveData<>();
    private final WebSocketManager.MessageListener messageListener;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
        requestRepository = new RequestRepository();
        messageListener = (type, payload) -> {
            try {
                JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
                String eventType = type != null ? type : (jsonObject.has("type") ? jsonObject.get("type").getAsString() : "");
                if ("NEW_MESSAGE".equals(eventType)) {
                    ChatMessage message = new Gson().fromJson(payload, ChatMessage.class);
                    if (message != null && message.getChatRoomId() != null) {
                        newMessage.postValue(message);
                    }
                    return;
                }
                String chatRoomId = jsonObject.has("chatRoomId") && !jsonObject.get("chatRoomId").isJsonNull()
                        ? jsonObject.get("chatRoomId").getAsString() : null;
                String requestId = jsonObject.has("requestId") && !jsonObject.get("requestId").isJsonNull()
                        ? jsonObject.get("requestId").getAsString() : null;
                chatEvent.postValue(new ChatEvent(eventType, chatRoomId, requestId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        WebSocketManager.getInstance().addMessageListener(messageListener);
    }

    public LiveData<Resource<Map<String, Object>>> participate(String requestId) {
        return chatRepository.participate(requestId);
    }

    public LiveData<Resource<List<ChatMessage>>> loadMessages(String roomId) {
        return chatRepository.getMessages(roomId);
    }

    public LiveData<Resource<List<ChatRoom>>> loadChatRooms() {
        return chatRepository.getChatRooms();
    }

    public LiveData<Resource<PartnerRequest>> loadRequestDetail(String requestId) {
        return requestRepository.getRequestDetail(requestId);
    }

    public LiveData<Resource<ChatRoom>> openChatRoom(String requestId) {
        return chatRepository.openChatRoom(requestId);
    }

    public LiveData<Resource<ChatMessage>> sendMessage(String roomId, String content) {
        return chatRepository.sendMessage(roomId, content);
    }

    public MutableLiveData<ChatMessage> getNewMessage() {
        return newMessage;
    }

    public MutableLiveData<ChatEvent> getChatEvent() {
        return chatEvent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        WebSocketManager.getInstance().removeMessageListener(messageListener);
    }
}
