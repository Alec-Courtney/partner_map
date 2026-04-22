package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.PartnerRequest;

import java.util.List;

public class RequestListViewModel extends ViewModel {
    private final RequestRepository requestRepository;

    public RequestListViewModel() {
        requestRepository = new RequestRepository();
    }

    public LiveData<Resource<List<PartnerRequest>>> loadRequests(double lat, double lng, int radius,
                                                                   Integer category, String schoolId, String timeFilter, int page) {
        return requestRepository.getNearbyRequests(lat, lng, radius, category, schoolId, timeFilter, page);
    }
}
