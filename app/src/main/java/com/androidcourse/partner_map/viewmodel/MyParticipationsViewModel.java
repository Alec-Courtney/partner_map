package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.EvaluationRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.Participation;

import java.util.List;

public class MyParticipationsViewModel extends ViewModel {
    private final EvaluationRepository evaluationRepository;

    public MyParticipationsViewModel() {
        evaluationRepository = new EvaluationRepository();
    }

    public LiveData<Resource<List<Participation>>> loadMyParticipations() {
        return evaluationRepository.getMyParticipations();
    }
}
