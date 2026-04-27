package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.model.User;
import com.androidcourse.partner_map.util.CategoryHelper;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.util.TimeUtil;
import com.androidcourse.partner_map.view.adapter.UserListAdapter;
import com.androidcourse.partner_map.viewmodel.RequestDetailViewModel;

public class RequestDetailActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";
    private RequestDetailViewModel viewModel;
    private TextView tvCategory, tvTitle, tvLocation, tvTime, tvPeople, tvGender, tvCost, tvDesc;
    private Button btnAction;
    private LinearLayout layoutPublisherActions;
    private String requestId;
    private PartnerRequest currentRequest;
    private String currentUserId;
    private UserListAdapter participantsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        viewModel = new ViewModelProvider(this).get(RequestDetailViewModel.class);
        requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        currentUserId = SharedPreferencesUtil.getInstance(this).getString("user_id", "");

        ImageView ivBack = findViewById(R.id.iv_back);
        tvCategory = findViewById(R.id.tv_category);
        tvTitle = findViewById(R.id.tv_title);
        tvLocation = findViewById(R.id.tv_location);
        tvTime = findViewById(R.id.tv_time);
        tvPeople = findViewById(R.id.tv_people);
        tvGender = findViewById(R.id.tv_gender);
        tvCost = findViewById(R.id.tv_cost);
        tvDesc = findViewById(R.id.tv_description);
        btnAction = findViewById(R.id.btn_action);
        layoutPublisherActions = findViewById(R.id.layout_publisher_actions);
        RecyclerView rvParticipants = findViewById(R.id.rv_participants);
        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        participantsAdapter = new UserListAdapter(null);
        rvParticipants.setAdapter(participantsAdapter);

        ivBack.setOnClickListener(v -> finish());
        btnAction.setOnClickListener(v -> onActionClick());

        loadDetail();
    }

    private void loadDetail() {
        viewModel.getRequestDetail(requestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                currentRequest = resource.data;
                bindData(currentRequest);
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(PartnerRequest req) {
        tvCategory.setText(CategoryHelper.getLabel(req.getCategory()));
        tvTitle.setText(req.getTitle());
        tvLocation.setText(String.format("地点: %.4f, %.4f", req.getRequestLat(), req.getRequestLng()));
        tvTime.setText("时间: " + TimeUtil.formatDateTime(req.getScheduledTime()));
        tvPeople.setText(String.format("人数: %d/%d", req.getCurrentParticipants(), req.getMaxParticipants()));
        tvGender.setText("性别: " + getGenderRequirementLabel(req.getGenderRequirement()));
        tvCost.setText("费用: " + (req.getCostDescription() == null || req.getCostDescription().isEmpty() ? "未说明" : req.getCostDescription()));
        tvDesc.setText(req.getDescription());

        if (req.getParticipants() != null) {
            participantsAdapter = new UserListAdapter(req.getParticipants());
            RecyclerView rvParticipants = findViewById(R.id.rv_participants);
            rvParticipants.setAdapter(participantsAdapter);
        }

        boolean isPublisher = currentUserId.equals(req.getPublisherId());
        layoutPublisherActions.setVisibility(isPublisher ? android.view.View.VISIBLE : android.view.View.GONE);

        if (isPublisher) {
            btnAction.setText("管理需求");
        } else {
            if (req.getStatus() == 0) {
                btnAction.setText("私信发起者");
            } else if (req.getStatus() == 1) {
                btnAction.setText("已满员");
                btnAction.setEnabled(false);
            } else {
                btnAction.setText("已结束");
                btnAction.setEnabled(false);
            }
        }
    }

    private void onActionClick() {
        if (currentRequest == null) return;
        boolean isPublisher = currentUserId.equals(currentRequest.getPublisherId());
        if (isPublisher) {
            // publisher actions handled by buttons in layout
        } else {
            if (currentRequest.getStatus() == 0) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_REQUEST_ID, currentRequest.getRequestId());
                intent.putExtra(ChatActivity.EXTRA_PUBLISHER_ID, currentRequest.getPublisherId());
                intent.putExtra(ChatActivity.EXTRA_PUBLISHER_NAME, currentRequest.getPublisherName());
                startActivity(intent);
            }
        }
    }

    private String getGenderRequirementLabel(int requirement) {
        switch (requirement) {
            case 1: return "仅男";
            case 2: return "仅女";
            default: return "不限";
        }
    }
}
