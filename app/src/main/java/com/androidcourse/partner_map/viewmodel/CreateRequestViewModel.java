package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.PartnerRequest;

public class CreateRequestViewModel extends ViewModel {
    private final RequestRepository requestRepository;

    public CreateRequestViewModel() {
        requestRepository = new RequestRepository();
    }

    public LiveData<Resource<PartnerRequest>> createRequest(PartnerRequest request) {
        return requestRepository.createRequest(request);
    }

    public LiveData<Resource<PartnerRequest>> updateRequest(String requestId, PartnerRequest request) {
        return requestRepository.updateRequest(requestId, request);
    }

    public LiveData<Resource<PartnerRequest>> getRequestDetail(String requestId) {
        return requestRepository.getRequestDetail(requestId);
    }
}
