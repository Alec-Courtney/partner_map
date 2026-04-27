package com.androidcourse.partner_map.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.model.School;
import com.androidcourse.partner_map.model.User;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class UserRepository {
    private final ApiClient apiClient;
    private final SharedPreferencesUtil prefs;

    public UserRepository(Context context) {
        apiClient = ApiClient.getInstance();
        prefs = SharedPreferencesUtil.getInstance(context);
    }

    public LiveData<Resource<User>> login(String nickname, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("nickname", nickname);
                body.put("password", password);
                retrofit2.Response<ApiResponse<User>> response = apiClient.getApiService().login(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    String token = user.getToken() != null ? user.getToken() : user.getUserId();
                    apiClient.setToken(token);
                    prefs.putString(Constants.KEY_TOKEN, token);
                    prefs.putString(Constants.KEY_USER_ID, user.getUserId());
                    prefs.putObject(Constants.KEY_USER_JSON, user);
                    result.postValue(Resource.success(user));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "登录失败";
                    result.postValue(Resource.error(msg, null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<User>> register(String nickname, String password, String gender, String schoolId, String avatar) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("nickname", nickname);
                body.put("password", password);
                body.put("gender", gender);
                body.put("schoolId", schoolId);
                body.put("avatar", avatar);
                retrofit2.Response<ApiResponse<User>> response = apiClient.getApiService().register(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User regUser = response.body().getData();
                    String token = regUser.getToken() != null ? regUser.getToken() : regUser.getUserId();
                    apiClient.setToken(token);
                    prefs.putString(Constants.KEY_TOKEN, token);
                    prefs.putString(Constants.KEY_USER_ID, regUser.getUserId());
                    prefs.putObject(Constants.KEY_USER_JSON, regUser);
                    result.postValue(Resource.success(regUser));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "注册失败";
                    result.postValue(Resource.error(msg, null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<School>>> getSchools() {
        MutableLiveData<Resource<List<School>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<School>>> response = apiClient.getApiService().getSchools().execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<School> list = response.body().getData();
                    prefs.putObject("schools_cache", list);
                    result.postValue(Resource.success(list));
                } else {
                    List<School> cached = prefs.getObject("schools_cache", java.util.ArrayList.class);
                    result.postValue(Resource.error("服务器错误", cached));
                }
            } catch (Exception e) {
                List<School> cached = prefs.getObject("schools_cache", java.util.ArrayList.class);
                result.postValue(Resource.error("网络错误", cached));
            }
        });
        return result;
    }

    public void logout() {
        apiClient.setToken(null);
        prefs.clear();
    }

    public User getCachedUser() {
        return prefs.getObject(Constants.KEY_USER_JSON, User.class);
    }

    public boolean isLoggedIn() {
        return prefs.getString(Constants.KEY_TOKEN, null) != null;
    }
}
