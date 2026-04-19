package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.PartnerRequest;

import java.util.List;

public class MyRequestsViewModel extends ViewModel {
    private final RequestRepository requestRepository;

    public MyRequestsViewModel() {
        requestRepository = new RequestRepository();
    }

    public LiveData<Resource<List<PartnerRequest>>> loadMyRequests() {
        return requestRepository.getMyRequests();
    }

    public LiveData<Resource<Void>> cancelRequest(String requestId) {
        return requestRepository.cancelRequest(requestId);
    }

    public LiveData<Resource<Void>> completeRequest(String requestId) {
        return requestRepository.completeRequest(requestId);
    }
}
