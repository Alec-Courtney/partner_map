package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.ChatRepository;
import com.androidcourse.partner_map.data.repository.ParticipationRepository;
import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.model.Participation;
import com.androidcourse.partner_map.model.PartnerRequest;

import java.util.Map;
import java.util.List;

public class RequestDetailViewModel extends ViewModel {
    private final RequestRepository requestRepository;
    private final ParticipationRepository participationRepository;
    private final ChatRepository chatRepository;

    public RequestDetailViewModel() {
        requestRepository = new RequestRepository();
        participationRepository = new ParticipationRepository();
        chatRepository = new ChatRepository();
    }

    public LiveData<Resource<PartnerRequest>> getRequestDetail(String requestId) {
        return requestRepository.getRequestDetail(requestId);
    }

    public LiveData<Resource<List<Participation>>> getParticipations(String requestId, Integer status) {
        return participationRepository.getParticipations(requestId, status);
    }

    public LiveData<Resource<Void>> cancelRequest(String requestId) {
        return requestRepository.cancelRequest(requestId);
    }

    public LiveData<Resource<Void>> completeRequest(String requestId) {
        return requestRepository.completeRequest(requestId);
    }

    public LiveData<Resource<Void>> approveParticipation(String participationId) {
        return participationRepository.approveParticipation(participationId);
    }

    public LiveData<Resource<Void>> rejectParticipation(String participationId) {
        return participationRepository.rejectParticipation(participationId);
    }

    public LiveData<Resource<Map<String, Object>>> participate(String requestId) {
        return chatRepository.participate(requestId);
    }

    public LiveData<Resource<ChatRoom>> openChatRoom(String requestId) {
        return chatRepository.openChatRoom(requestId);
    }
}
