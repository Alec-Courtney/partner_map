package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.data.remote.PaginatedData;
import com.androidcourse.partner_map.model.Participation;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRepository extends BaseRepository {

    public LiveData<Resource<List<Participation>>> getMyParticipations(int page, int size) {
        MutableLiveData<Resource<List<Participation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<Participation>>> response =
                        apiClient.getApiService().getMyParticipations(page, size).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<Participation> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload.getItems()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载我的参与失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Participation>>> getParticipations(String requestId, Integer status) {
        MutableLiveData<Resource<List<Participation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<Participation>>> response =
                        apiClient.getApiService().getParticipations(requestId, status).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Participation> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载参与列表失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> approveParticipation(String participationId) {
        return changeStatus(participationId, true);
    }

    public LiveData<Resource<Void>> rejectParticipation(String participationId) {
        return changeStatus(participationId, false);
    }

    private LiveData<Resource<Void>> changeStatus(String participationId, boolean approve) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = approve
                        ? apiClient.getApiService().approveParticipation(participationId).execute()
                        : apiClient.getApiService().rejectParticipation(participationId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, approve ? "同意失败" : "拒绝失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }
}
