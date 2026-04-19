package com.androidcourse.partner_map.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.androidcourse.partner_map.data.repository.UserRepository;
import com.androidcourse.partner_map.model.User;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<User> getUser() {
        User cached = userRepository.getCachedUser();
        userLiveData.setValue(cached);
        return userLiveData;
    }

    public void logout() {
        userRepository.logout();
    }
}
