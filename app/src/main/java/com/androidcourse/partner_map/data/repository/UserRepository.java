package com.androidcourse.partner_map.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.data.remote.ApiResponse;
import com.androidcourse.partner_map.model.School;
import com.androidcourse.partner_map.model.User;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository extends BaseRepository {
    private final SharedPreferencesUtil prefs;

    public UserRepository(Context context) {
        prefs = SharedPreferencesUtil.getInstance(context);
    }

    public LiveData<Resource<User>> login(String nickname, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("nickname", nickname);
                body.put("password", password);
                retrofit2.Response<ApiResponse<User>> response = apiClient.getApiService().login(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    persistSession(user);
                    result.postValue(Resource.success(user));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "登录失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<User>> register(String nickname, String password, int gender, String schoolId, String avatar) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("nickname", nickname);
                body.put("password", password);
                body.put("gender", String.valueOf(gender));
                body.put("schoolId", schoolId);
                if (avatar != null && !avatar.trim().isEmpty()) {
                    body.put("avatar", avatar);
                }
                retrofit2.Response<ApiResponse<User>> response = apiClient.getApiService().register(body).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    persistSession(user);
                    result.postValue(Resource.success(user));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "注册失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<School>>> getSchools() {
        return getSchools(null);
    }

    public LiveData<Resource<List<School>>> getSchools(String city) {
        MutableLiveData<Resource<List<School>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<List<School>>> response = apiClient.getApiService().getSchools(city).execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.postValue(Resource.success(response.body().getData()));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载学校失败"), null));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<User>> getCurrentUser() {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(getCachedUser()));
        IO.execute(() -> {
            try {
                retrofit2.Response<ApiResponse<User>> response = apiClient.getApiService().getCurrentUser().execute();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        User cached = getCachedUser();
                        if (cached != null && (user.getToken() == null || user.getToken().isEmpty())) {
                            user.setToken(cached.getToken());
                        }
                        persistSession(user);
                    }
                    result.postValue(Resource.success(user));
                } else {
                    result.postValue(Resource.error(getErrorMessage(response, "加载用户信息失败"), getCachedUser()));
                }
            } catch (Exception e) {
                result.postValue(Resource.error("网络错误: " + e.getMessage(), getCachedUser()));
            }
        });
        return result;
    }

    public User getCachedUser() {
        return prefs.getObject(Constants.KEY_USER_JSON, User.class);
    }

    public boolean isLoggedIn() {
        String token = prefs.getString(Constants.KEY_TOKEN, null);
        return token != null && !token.trim().isEmpty();
    }

    public void logout() {
        apiClient.setToken(null);
        prefs.clear();
    }

    private void persistSession(User user) {
        if (user == null) {
            return;
        }
        String token = user.getToken();
        if (token != null && !token.trim().isEmpty()) {
            apiClient.setToken(token);
            prefs.putString(Constants.KEY_TOKEN, token);
        }
        if (user.getUserId() != null) {
            prefs.putString(Constants.KEY_USER_ID, user.getUserId());
        }
        prefs.putObject(Constants.KEY_USER_JSON, user);
    }
}
