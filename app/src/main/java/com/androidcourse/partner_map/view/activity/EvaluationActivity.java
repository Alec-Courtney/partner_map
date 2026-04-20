package com.androidcourse.partner_map.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Evaluation;
import com.androidcourse.partner_map.view.adapter.EvaluationAdapter;
import com.androidcourse.partner_map.viewmodel.EvaluationViewModel;

import java.util.ArrayList;
import java.util.List;

public class EvaluationActivity extends AppCompatActivity {
    private EvaluationViewModel viewModel;
    private EvaluationAdapter adapter;
    private final List<Evaluation> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        viewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EvaluationAdapter(data, this::submit);
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
        loadData();
    }

    private void loadData() {
        viewModel.loadPendingEvaluations().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                data.clear();
                data.addAll(resource.data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void submit(Evaluation evaluation) {
        viewModel.submitEvaluation(evaluation).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "评价成功", Toast.LENGTH_SHORT).show();
                loadData();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
