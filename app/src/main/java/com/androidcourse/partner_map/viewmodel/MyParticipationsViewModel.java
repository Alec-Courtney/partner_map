package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.ParticipationRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.Participation;

import java.util.List;

public class MyParticipationsViewModel extends ViewModel {
    private final ParticipationRepository participationRepository;

    public MyParticipationsViewModel() {
        participationRepository = new ParticipationRepository();
    }

    public LiveData<Resource<List<Participation>>> loadMyParticipations() {
        return participationRepository.getMyParticipations(1, 50);
    }
}
