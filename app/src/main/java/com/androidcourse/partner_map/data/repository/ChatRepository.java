package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.model.ChatRoom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class ChatRepository {
    private final ApiClient apiClient;

    public ChatRepository() {
        apiClient = ApiClient.getInstance();
    }

    public LiveData<Resource<Map<String, Object>>> participate(String requestId) {
        MutableLiveData<Resource<Map<String, Object>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Map<String, Object>>> response =
                        apiClient.getApiService().participate(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error("参与失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<ChatRoom>>> getChatRooms() {
        MutableLiveData<Resource<List<ChatRoom>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<ChatRoom>>> response = apiClient.getApiService().getChatRooms().execute();
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

    public LiveData<Resource<ChatRoom>> createChatRoom(String requestId, String requesterId) {
        MutableLiveData<Resource<ChatRoom>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Map<String, String> body = new HashMap<>();
                body.put("requestId", requestId);
                body.put("requesterId", requesterId);
                retrofit2.Response<ApiResponse<ChatRoom>> response = apiClient.getApiService().createChatRoom(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error("创建失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<ChatMessage>>> getMessages(String roomId) {
        MutableLiveData<Resource<List<ChatMessage>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<ChatMessage>>> response = apiClient.getApiService().getMessages(roomId).execute();
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

    public LiveData<Resource<ChatMessage>> sendMessage(String roomId, String content) {
        MutableLiveData<Resource<ChatMessage>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Map<String, String> body = new HashMap<>();
                body.put("content", content);
                retrofit2.Response<ApiResponse<ChatMessage>> response = apiClient.getApiService().sendMessage(roomId, body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error("发送失败", null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误", null));
            }
        });
        return result;
    }
}
