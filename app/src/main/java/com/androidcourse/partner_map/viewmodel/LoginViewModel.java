package com.androidcourse.partner_map.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.data.repository.UserRepository;
import com.androidcourse.partner_map.model.User;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Resource<User>> login(String nickname, String password) {
        LiveData<Resource<User>> result = userRepository.login(nickname, password);
        result.observeForever(resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                WebSocketManager.getInstance().connect(resource.data.getUserId());
            }
        });
        return result;
    }

    public boolean isLoggedIn() {
        return userRepository.isLoggedIn();
    }
}
