package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.RequestRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.PartnerRequest;

import java.util.List;

public class MapViewModel extends ViewModel {
    private final RequestRepository requestRepository;
    private final MutableLiveData<List<PartnerRequest>> requestList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isMapMode = new MutableLiveData<>(true);

    public MapViewModel() {
        requestRepository = new RequestRepository();
    }

    public LiveData<Resource<List<PartnerRequest>>> loadRequests(double lat, double lng, int radius,
                                                                  Integer category, String schoolId,
                                                                  String schoolFilter, String timeFilter) {
        return requestRepository.getNearbyRequests(lat, lng, radius, category, schoolId, schoolFilter, timeFilter, 1);
    }

    public MutableLiveData<List<PartnerRequest>> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<PartnerRequest> list) {
        requestList.setValue(list);
    }

    public MutableLiveData<Boolean> getIsMapMode() {
        return isMapMode;
    }

    public void toggleMode() {
        Boolean current = isMapMode.getValue();
        isMapMode.setValue(current == null || !current);
    }
}
