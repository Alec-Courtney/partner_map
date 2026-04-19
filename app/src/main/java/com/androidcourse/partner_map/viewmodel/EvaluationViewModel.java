package com.androidcourse.partner_map.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.androidcourse.partner_map.data.repository.EvaluationRepository;
import com.androidcourse.partner_map.data.repository.Resource;
import com.androidcourse.partner_map.model.Evaluation;

import java.util.List;

public class EvaluationViewModel extends ViewModel {
    private final EvaluationRepository evaluationRepository;

    public EvaluationViewModel() {
        evaluationRepository = new EvaluationRepository();
    }

    public LiveData<Resource<List<Evaluation>>> loadPendingEvaluations() {
        return evaluationRepository.getPendingEvaluations();
    }

    public LiveData<Resource<Void>> submitEvaluation(Evaluation evaluation) {
        return evaluationRepository.submitEvaluation(evaluation);
    }
}
