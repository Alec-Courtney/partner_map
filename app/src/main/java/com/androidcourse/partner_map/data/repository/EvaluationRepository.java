package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.data.remote.PaginatedData;
import com.androidcourse.partner_map.model.Evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationRepository extends BaseRepository {

    public LiveData<Resource<List<Evaluation>>> getPendingEvaluations() {
        MutableLiveData<Resource<List<Evaluation>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<Evaluation>>> response =
                        apiClient.getApiService().getPendingEvaluations(1, 50).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<Evaluation> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload.getItems()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载待评价列表失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> submitEvaluation(Evaluation evaluation) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("requestId", evaluation.getRequestId());
                body.put("toUserId", evaluation.getToUserId());
                body.put("attended", evaluation.isAttended());
                body.put("praised", evaluation.isPraised());
                retrofit2.Response<ApiResponse<Void>> response =
                        apiClient.getApiService().submitEvaluation(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "提交评价失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }
}
