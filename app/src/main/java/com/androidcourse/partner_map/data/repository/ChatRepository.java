package com.androidcourse.partner_map.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.data.remote.PaginatedData;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.model.ChatRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository extends BaseRepository {

    public LiveData<Resource<Map<String, Object>>> participate(String requestId) {
        MutableLiveData<Resource<Map<String, Object>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<Map<String, Object>>> response =
                        apiClient.getApiService().participate(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "申请参与失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<ChatRoom>>> getChatRooms() {
        MutableLiveData<Resource<List<ChatRoom>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<ChatRoom>>> response =
                        apiClient.getApiService().getChatRooms().execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ChatRoom> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载聊天室失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<ChatRoom>> openChatRoom(String requestId) {
        MutableLiveData<Resource<ChatRoom>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<ChatRoom>> response =
                        apiClient.getApiService().openChatRoom(requestId).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "进入私聊失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<ChatMessage>>> getMessages(String roomId) {
        MutableLiveData<Resource<List<ChatMessage>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<PaginatedData<ChatMessage>>> response =
                        apiClient.getApiService().getMessages(roomId, 1, 100).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PaginatedData<ChatMessage> payload = response.body().getData();
                    result.postValue(Resource.success(payload == null ? new ArrayList<>() : payload.getItems()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载消息失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<ChatMessage>> sendMessage(String roomId, String content) {
        MutableLiveData<Resource<ChatMessage>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                Map<String, String> body = new HashMap<>();
                body.put("content", content);
                retrofit2.Response<ApiResponse<ChatMessage>> response =
                        apiClient.getApiService().sendMessage(roomId, body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "发送消息失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }
}
