package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.view.adapter.HistoryAdapter;
import com.androidcourse.partner_map.viewmodel.MyRequestsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {
    private MyRequestsViewModel viewModel;
    private HistoryAdapter adapter;
    private final List<PartnerRequest> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        viewModel = new ViewModelProvider(this).get(MyRequestsViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(data, true, new HistoryAdapter.OnActionListener() {
            @Override
            public void onView(PartnerRequest request) {
                Intent intent = new Intent(MyRequestsActivity.this, RequestDetailActivity.class);
                intent.putExtra(RequestDetailActivity.EXTRA_REQUEST_ID, request.getRequestId());
                startActivity(intent);
            }

            @Override
            public void onEdit(PartnerRequest request) {
                // open edit
            }

            @Override
            public void onCancel(PartnerRequest request) {
                viewModel.cancelRequest(request.getRequestId()).observe(MyRequestsActivity.this, resource -> {
                    if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                        Toast.makeText(MyRequestsActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                });
            }

            @Override
            public void onComplete(PartnerRequest request) {
                viewModel.completeRequest(request.getRequestId()).observe(MyRequestsActivity.this, resource -> {
                    if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                        Toast.makeText(MyRequestsActivity.this, "已标记完成", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
        loadData();
    }

    private void loadData() {
        viewModel.loadMyRequests().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                data.clear();
                data.addAll(resource.data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
