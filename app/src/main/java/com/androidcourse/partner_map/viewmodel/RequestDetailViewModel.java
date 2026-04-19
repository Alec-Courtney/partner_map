package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.ChatRepository;
import com.androidcourse.partner_map.data.repository.EvaluationRepository;
import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.model.PartnerRequest;

public class RequestDetailViewModel extends ViewModel {
    private final RequestRepository requestRepository;
    private final ChatRepository chatRepository;
    private final EvaluationRepository evaluationRepository;

    public RequestDetailViewModel() {
        requestRepository = new RequestRepository();
        chatRepository = new ChatRepository();
        evaluationRepository = new EvaluationRepository();
    }

    public LiveData<Resource<PartnerRequest>> getRequestDetail(String requestId) {
        return requestRepository.getRequestDetail(requestId);
    }

    public LiveData<Resource<ChatRoom>> createChatRoom(String requestId, String requesterId) {
        return chatRepository.createChatRoom(requestId, requesterId);
    }

    public LiveData<Resource<Void>> cancelRequest(String requestId) {
        return requestRepository.cancelRequest(requestId);
    }

    public LiveData<Resource<Void>> completeRequest(String requestId) {
        return requestRepository.completeRequest(requestId);
    }
}
