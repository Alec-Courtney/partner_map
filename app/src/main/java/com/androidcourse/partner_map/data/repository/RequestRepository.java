package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.data.remote.PaginatedData;
import com.androidcourse.partner_map.model.PartnerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RequestRepository {
    private final ApiClient apiClient;

    public RequestRepository() {
        apiClient = ApiClient.getInstance();
    }

    public LiveData<Resource<List<PartnerRequest>>> getNearbyRequests(double lat, double lng, int radius, Integer category, String schoolId, String timeFilter, int page) {
        MutableLiveData<Resource<List<PartnerRequest>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<PartnerRequest>>> response = apiClient.getApiService()
                        .getRequests(lat, lng, radius, category, schoolId, timeFilter, page, 20).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<PartnerRequest> pageData = response.body().getData();
                    List<PartnerRequest> items = pageData != null ? pageData.getItems() : null;
                    result.postValue(Resource.success(items));
                } else {
                    result.postValue(Resource.error("加载失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<PartnerRequest>> createRequest(PartnerRequest request) {
        MutableLiveData<Resource<PartnerRequest>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PartnerRequest>> response = apiClient.getApiService().createRequest(request).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    int httpCode = response.code();
                    String serverMsg = response.body() != null ? response.body().getMessage() : "未知错误";
                    String msg = "发布失败 (HTTP " + httpCode + "): " + serverMsg;
                    result.postValue(Resource.error(msg, null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<PartnerRequest>> getRequestDetail(String requestId) {
        MutableLiveData<Resource<PartnerRequest>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PartnerRequest>> response = apiClient.getApiService().getRequestDetail(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error("加载失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<PartnerRequest>>> getMyRequests() {
        MutableLiveData<Resource<List<PartnerRequest>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<PartnerRequest>>> response = apiClient.getApiService().getMyRequests().execute();
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        PaginatedData<PartnerRequest> pageData = response.body().getData();
                        List<PartnerRequest> items = pageData != null ? pageData.getItems() : new ArrayList<>();
                        result.postValue(Resource.success(items));
                    } else {
                        int code = response.body().getCode();
                        if (code == 3001) {
                            result.postValue(Resource.success(new ArrayList<>()));
                        } else {
                            result.postValue(Resource.error(response.body().getMessage(), null));
                        }
                    }
                } else {
                    result.postValue(Resource.error("加载失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> cancelRequest(String requestId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().cancelRequest(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("操作失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> completeRequest(String requestId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().completeRequest(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("操作失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }
}
