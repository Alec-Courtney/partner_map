package com.androidcourse.partner_map.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.data.repository.UserRepository;
import com.androidcourse.partner_map.model.School;
import com.androidcourse.partner_map.model.User;

import java.util.List;

public class RegisterViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Resource<User>> register(String nickname, String password, int gender, String schoolId, String avatar) {
        return userRepository.register(nickname, password, gender, schoolId, avatar);
    }

    public LiveData<Resource<List<School>>> getSchools() {
        return userRepository.getSchools();
    }
}
