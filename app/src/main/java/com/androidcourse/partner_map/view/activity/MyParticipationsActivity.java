package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Participation;
import com.androidcourse.partner_map.view.adapter.ParticipationAdapter;
import com.androidcourse.partner_map.viewmodel.MyParticipationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyParticipationsActivity extends AppCompatActivity {
    private MyParticipationsViewModel viewModel;
    private ParticipationAdapter adapter;
    private final List<Participation> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_participations);

        viewModel = new ViewModelProvider(this).get(MyParticipationsViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParticipationAdapter(data, participation -> {
            Intent intent = new Intent(this, RequestDetailActivity.class);
            intent.putExtra(RequestDetailActivity.EXTRA_REQUEST_ID, participation.getRequestId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
        loadData();
    }

    private void loadData() {
        viewModel.loadMyParticipations().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                data.clear();
                data.addAll(resource.data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
