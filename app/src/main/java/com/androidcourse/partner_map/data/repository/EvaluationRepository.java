package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.model.Evaluation;
import com.androidcourse.partner_map.model.Participation;

import java.util.List;
import java.util.concurrent.Executors;

public class EvaluationRepository {
    private final ApiClient apiClient;

    public EvaluationRepository() {
        apiClient = ApiClient.getInstance();
    }

    public LiveData<Resource<List<Evaluation>>> getPendingEvaluations() {
        MutableLiveData<Resource<List<Evaluation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<Evaluation>>> response = apiClient.getApiService().getPendingEvaluations().execute();
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

    public LiveData<Resource<Void>> submitEvaluation(Evaluation evaluation) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().submitEvaluation(evaluation).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("提交失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Participation>>> getMyParticipations() {
        MutableLiveData<Resource<List<Participation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<Participation>>> response = apiClient.getApiService().getMyParticipations().execute();
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

    public LiveData<Resource<Void>> approveParticipation(String participationId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().approveParticipation(participationId).execute();
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

    public LiveData<Resource<Void>> rejectParticipation(String participationId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Void>> response = apiClient.getApiService().rejectParticipation(participationId).execute();
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
