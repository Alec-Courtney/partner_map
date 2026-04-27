package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.data.repository.ChatRepository;
import com.androidcourse.partner_map.data.repository.EvaluationRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.ChatMessage;

import java.util.List;
import java.util.Map;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private final EvaluationRepository evaluationRepository;
    private final MutableLiveData<ChatMessage> newMessage = new MutableLiveData<>();
    private final WebSocketManager.MessageListener messageListener;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
        evaluationRepository = new EvaluationRepository();
        messageListener = (type, payload) -> {
            // In real app, parse payload to ChatMessage
        };
        WebSocketManager.getInstance().addMessageListener(messageListener);
    }

    public LiveData<Resource<Map<String, Object>>> participate(String requestId) {
        return chatRepository.participate(requestId);
    }

    public LiveData<Resource<List<ChatMessage>>> loadMessages(String roomId) {
        return chatRepository.getMessages(roomId);
    }

    public LiveData<Resource<ChatMessage>> sendMessage(String roomId, String content) {
        return chatRepository.sendMessage(roomId, content);
    }

    public LiveData<Resource<Void>> approveParticipation(String participationId) {
        return evaluationRepository.approveParticipation(participationId);
    }

    public LiveData<Resource<Void>> rejectParticipation(String participationId) {
        return evaluationRepository.rejectParticipation(participationId);
    }

    public MutableLiveData<ChatMessage> getNewMessage() {
        return newMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        WebSocketManager.getInstance().removeMessageListener(messageListener);
    }
}
