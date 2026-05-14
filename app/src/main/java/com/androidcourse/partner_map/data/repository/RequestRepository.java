package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.data.remote.PaginatedData;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestRepository extends BaseRepository {

    public LiveData<Resource<List<PartnerRequest>>> getNearbyRequests(double lat, double lng, Integer radius,
                                                                      Integer category, String schoolId,
                                                                      String schoolFilter, String timeRange, int page) {
        MutableLiveData<Resource<List<PartnerRequest>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                String categoryParam = category == null ? null : String.valueOf(category);
                retrofit2.Response<ApiResponse<PaginatedData<PartnerRequest>>> response = apiClient.getApiService()
                        .getRequests(lat, lng, radius, categoryParam, schoolId, schoolFilter, timeRange,
                                Math.max(page, 1), Constants.PAGE_SIZE)
                        .execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<PartnerRequest> pageData = response.body().getData();
                    List<PartnerRequest> items = pageData == null || pageData.getItems() == null
                            ? new ArrayList<>() : pageData.getItems();
                    for (PartnerRequest item : items) {
                        float[] results = new float[1];
                        android.location.Location.distanceBetween(
                                lat, lng, item.getRequestLat(), item.getRequestLng(), results
                        );
                        item.setDistanceMeters(results[0]);
                    }
                    result.postValue(Resource.success(items));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载需求失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<PartnerRequest>> createRequest(PartnerRequest request) {
        MutableLiveData<Resource<PartnerRequest>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PartnerRequest>> response =
                        apiClient.getApiService().createRequest(toRequestBody(request)).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "发布需求失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<PartnerRequest>> updateRequest(String requestId, PartnerRequest request) {
        MutableLiveData<Resource<PartnerRequest>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PartnerRequest>> response =
                        apiClient.getApiService().updateRequest(requestId, toRequestBody(request)).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "更新需求失败"), null));
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
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PartnerRequest>> response =
                        apiClient.getApiService().getRequestDetail(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载需求详情失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<PartnerRequest>>> getMyRequests() {
        return getMyRequests(1, Constants.PAGE_SIZE);
    }

    public LiveData<Resource<List<PartnerRequest>>> getMyRequests(int page, int size) {
        MutableLiveData<Resource<List<PartnerRequest>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<PartnerRequest>>> response =
                        apiClient.getApiService().getMyRequests(page, size).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<PartnerRequest> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload.getItems()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载我的需求失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> cancelRequest(String requestId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().cancelRequest(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "取消需求失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> completeRequest(String requestId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().completeRequest(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "标记完成失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    private Map<String, Object> toRequestBody(PartnerRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", request.getTitle());
        body.put("description", request.getDescription());
        body.put("category", request.getCategory());
        body.put("requestLat", request.getRequestLat());
        body.put("requestLng", request.getRequestLng());
        if (!request.getRequestAddress().isEmpty()) {
            body.put("requestAddress", request.getRequestAddress());
        }
        body.put("publishLat", request.getPublishLat());
        body.put("publishLng", request.getPublishLng());
        body.put("maxParticipants", request.getMaxParticipants());
        body.put("scheduledTime", TimeUtil.toApiDateTime(request.getScheduledTime()));
        body.put("expireBeforeMin", request.getExpireBeforeMin());
        body.put("genderRequirement", request.getGenderRequirement());
        if (!request.getCostDescription().isEmpty()) {
            body.put("costDescription", request.getCostDescription());
        }
        return body;
    }
}
