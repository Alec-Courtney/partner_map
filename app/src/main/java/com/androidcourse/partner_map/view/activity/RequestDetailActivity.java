package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.model.Participation;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.CategoryHelper;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.util.TimeUtil;
import com.androidcourse.partner_map.view.adapter.ParticipationAdapter;
import com.androidcourse.partner_map.view.adapter.UserListAdapter;
import com.androidcourse.partner_map.viewmodel.RequestDetailViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestDetailActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";

    private RequestDetailViewModel viewModel;
    private TextView tvCategory;
    private TextView tvTitle;
    private TextView tvLocation;
    private TextView tvStatus;
    private TextView tvTime;
    private TextView tvPeople;
    private TextView tvGender;
    private TextView tvCost;
    private TextView tvDesc;
    private TextView tvPendingTitle;
    private MaterialButton btnPrimaryAction;
    private MaterialButton btnSecondaryAction;
    private LinearLayout layoutPublisherActions;
    private RecyclerView rvPendingParticipations;
    private String requestId;
    private String currentUserId;
    private PartnerRequest currentRequest;
    private UserListAdapter participantsAdapter;
    private ParticipationAdapter pendingAdapter;
    private final List<Participation> pendingParticipations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        viewModel = new ViewModelProvider(this).get(RequestDetailViewModel.class);
        requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        currentUserId = SharedPreferencesUtil.getInstance(this).getString(Constants.KEY_USER_ID, "");

        bindViews();
        bindActions();
        loadDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestId != null && !requestId.trim().isEmpty()) {
            loadDetail();
        }
    }

    private void bindViews() {
        MaterialToolbar toolbar = findViewById(R.id.iv_back);
        tvCategory = findViewById(R.id.tv_category);
        tvTitle = findViewById(R.id.tv_title);
        tvLocation = findViewById(R.id.tv_location);
        tvStatus = findViewById(R.id.tv_status);
        tvTime = findViewById(R.id.tv_time);
        tvPeople = findViewById(R.id.tv_people);
        tvGender = findViewById(R.id.tv_gender);
        tvCost = findViewById(R.id.tv_cost);
        tvDesc = findViewById(R.id.tv_description);
        tvPendingTitle = findViewById(R.id.tv_pending_title);
        btnPrimaryAction = findViewById(R.id.btn_action);
        btnSecondaryAction = findViewById(R.id.btn_secondary_action);
        layoutPublisherActions = findViewById(R.id.layout_publisher_actions);
        RecyclerView rvParticipants = findViewById(R.id.rv_participants);
        rvPendingParticipations = findViewById(R.id.rv_pending_participations);

        rvParticipants.setLayoutManager(new LinearLayoutManager(this));
        rvPendingParticipations.setLayoutManager(new LinearLayoutManager(this));

        participantsAdapter = new UserListAdapter(new ArrayList<>());
        pendingAdapter = new ParticipationAdapter(
                pendingParticipations,
                new ParticipationAdapter.OnActionListener() {
                    @Override
                    public void onApprove(Participation participation) {
                        viewModel.approveParticipation(participation.getParticipationId()).observe(RequestDetailActivity.this, resource -> {
                            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                                Toast.makeText(RequestDetailActivity.this, "已同意加入", Toast.LENGTH_SHORT).show();
                                loadDetail();
                            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                                Toast.makeText(RequestDetailActivity.this, resource.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onReject(Participation participation) {
                        viewModel.rejectParticipation(participation.getParticipationId()).observe(RequestDetailActivity.this, resource -> {
                            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                                Toast.makeText(RequestDetailActivity.this, "已拒绝加入", Toast.LENGTH_SHORT).show();
                                loadDetail();
                            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                                Toast.makeText(RequestDetailActivity.this, resource.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                participation -> openChatForTarget(
                        participation.getUserId(),
                        participation.getUserName(),
                        participation.getStatus()
                )
        );

        rvParticipants.setAdapter(participantsAdapter);
        rvPendingParticipations.setAdapter(pendingAdapter);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindActions() {
        btnPrimaryAction.setOnClickListener(v -> onPrimaryAction());
        btnSecondaryAction.setOnClickListener(v -> onSecondaryAction());

        findViewById(R.id.btn_edit).setOnClickListener(v -> {
            if (currentRequest == null) {
                return;
            }
            Intent intent = new Intent(this, CreateRequestActivity.class);
            intent.putExtra(CreateRequestActivity.EXTRA_REQUEST_ID, currentRequest.getRequestId());
            startActivity(intent);
        });

        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            if (currentRequest == null) {
                return;
            }
            viewModel.cancelRequest(currentRequest.getRequestId()).observe(this, resource -> {
                if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                    Toast.makeText(this, "需求已取消", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadDetail() {
        viewModel.getRequestDetail(requestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                currentRequest = resource.data;
                bindData(resource.data);
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindData(PartnerRequest request) {
        tvCategory.setText(CategoryHelper.getLabel(request.getCategory()));
        tvTitle.setText(request.getTitle());
        tvLocation.setText(buildLocationLabel(request));
        tvStatus.setText("状态: " + CategoryHelper.getStatusLabel(request.getStatus()));
        tvTime.setText("时间: " + TimeUtil.formatDateTime(request.getScheduledTime()));
        tvPeople.setText(String.format(Locale.CHINA, "已加入 %d / 上限 %d", request.getCurrentParticipants(), request.getMaxParticipants()));
        tvGender.setText("性别: " + getGenderRequirementLabel(request.getGenderRequirement()));
        tvCost.setText("费用: " + (request.getCostDescription().isEmpty() ? "未说明" : request.getCostDescription()));
        tvDesc.setText(buildDescription(request));
        boolean isPublisher = request.isPublisher() || currentUserId.equals(request.getPublisherId());

        if (isPublisher) {
            participantsAdapter = new UserListAdapter(
                    request.getParticipants() == null ? new ArrayList<>() : request.getParticipants(),
                    user -> openChatForTarget(user.getUserId(), user.getNickname(), 1)
            );
        } else {
            participantsAdapter = new UserListAdapter(request.getParticipants() == null ? new ArrayList<>() : request.getParticipants());
        }
        ((RecyclerView) findViewById(R.id.rv_participants)).setAdapter(participantsAdapter);

        if (isPublisher) {
            bindPublisherState(request);
            loadPendingParticipations(request.getRequestId());
        } else {
            bindUserState(request);
        }
    }

    private String buildLocationLabel(PartnerRequest request) {
        if (!request.getRequestAddress().isEmpty()) {
            return "地点: " + request.getRequestAddress();
        }
        return String.format(Locale.CHINA, "地点: %.6f, %.6f", request.getRequestLat(), request.getRequestLng());
    }

    private String buildDescription(PartnerRequest request) {
        if (request.getMySnapshotData() != null && !request.getMySnapshotData().trim().isEmpty()) {
            return request.getDescription() + "\n\n你已成功加入，系统已保存加入时的快照。";
        }
        return request.getDescription();
    }

    private void bindPublisherState(PartnerRequest request) {
        layoutPublisherActions.setVisibility(request.getStatus() == 0 ? View.VISIBLE : View.GONE);
        tvPendingTitle.setVisibility(request.getStatus() == 2 ? View.GONE : View.VISIBLE);
        rvPendingParticipations.setVisibility(request.getStatus() == 2 ? View.GONE : View.VISIBLE);
        btnSecondaryAction.setVisibility(View.GONE);
        btnPrimaryAction.setVisibility(View.VISIBLE);
        btnPrimaryAction.setEnabled(request.getStatus() != 2);
        btnPrimaryAction.setText(request.getStatus() == 2 ? "已结束" : "标记完成");
    }

    private void bindUserState(PartnerRequest request) {
        layoutPublisherActions.setVisibility(View.GONE);
        tvPendingTitle.setVisibility(View.GONE);
        rvPendingParticipations.setVisibility(View.GONE);
        btnPrimaryAction.setVisibility(View.VISIBLE);
        btnSecondaryAction.setVisibility(View.VISIBLE);

        Integer myStatus = request.getMyParticipationStatus();
        if (myStatus == null) {
            if (request.getStatus() == 0) {
                btnPrimaryAction.setText("进入私聊");
                btnPrimaryAction.setEnabled(true);
                btnSecondaryAction.setText("申请加入");
                btnSecondaryAction.setEnabled(true);
            } else if (request.getStatus() == 1) {
                btnPrimaryAction.setText("已满员，无法私聊");
                btnPrimaryAction.setEnabled(false);
                btnSecondaryAction.setText("已满员");
                btnSecondaryAction.setEnabled(false);
            } else {
                btnPrimaryAction.setText("已结束");
                btnPrimaryAction.setEnabled(false);
                btnSecondaryAction.setVisibility(View.GONE);
            }
            return;
        }

        btnPrimaryAction.setText("进入私聊");
        btnPrimaryAction.setEnabled(true);
        btnSecondaryAction.setEnabled(false);
        if (myStatus == 0) {
            btnSecondaryAction.setText("已申请，待审核");
        } else if (myStatus == 1) {
            btnSecondaryAction.setText("已加入");
        } else {
            btnSecondaryAction.setText("申请已拒绝");
        }
    }

    private void loadPendingParticipations(String targetRequestId) {
        viewModel.getParticipations(targetRequestId, 0).observe(this, resource -> {
            if (resource.status != com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                return;
            }
            pendingParticipations.clear();
            if (resource.data != null) {
                pendingParticipations.addAll(resource.data);
            }
            pendingAdapter.notifyDataSetChanged();
            boolean showPending = currentRequest != null && currentRequest.getStatus() != 2 && !pendingParticipations.isEmpty();
            tvPendingTitle.setVisibility(showPending ? View.VISIBLE : View.GONE);
            rvPendingParticipations.setVisibility(showPending ? View.VISIBLE : View.GONE);
        });
    }

    private void onPrimaryAction() {
        if (currentRequest == null) {
            return;
        }
        boolean isPublisher = currentRequest.isPublisher() || currentUserId.equals(currentRequest.getPublisherId());
        if (isPublisher) {
            if (currentRequest.getStatus() == 2) {
                return;
            }
            viewModel.completeRequest(currentRequest.getRequestId()).observe(this, resource -> {
                if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                    Toast.makeText(this, "已标记完成", Toast.LENGTH_SHORT).show();
                    loadDetail();
                } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        openChat(
                null,
                currentRequest.getMyParticipationStatus(),
                currentRequest.getStatus()
        );
    }

    private void onSecondaryAction() {
        if (currentRequest == null) {
            return;
        }
        Integer myStatus = currentRequest.getMyParticipationStatus();
        if (myStatus != null || currentRequest.getStatus() != 0) {
            return;
        }

        viewModel.participate(currentRequest.getRequestId()).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                Toast.makeText(this, "加入申请已提交，等待发起者审核", Toast.LENGTH_SHORT).show();
                Object roomId = resource.data.get("chatRoomId");
                openChat(roomId == null ? null : roomId.toString(), 0, currentRequest.getStatus());
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChat(String chatRoomId, Integer participationStatus, int requestStatus) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_REQUEST_ID, currentRequest.getRequestId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_ID, currentRequest.getPublisherId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_NAME, currentRequest.getPublisherName());
        intent.putExtra(ChatActivity.EXTRA_TARGET_USER_ID, currentRequest.getPublisherId());
        intent.putExtra(ChatActivity.EXTRA_REQUEST_STATUS, requestStatus);
        intent.putExtra(
                ChatActivity.EXTRA_PARTICIPATION_STATUS,
                participationStatus == null ? ChatActivity.PARTICIPATION_STATUS_NONE : participationStatus
        );
        if (chatRoomId != null && !chatRoomId.trim().isEmpty()) {
            intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID, chatRoomId);
        }
        startActivity(intent);
    }

    private void openChatForTarget(String targetUserId, String targetName, Integer participationStatus) {
        if (currentRequest == null || targetUserId == null || targetUserId.trim().isEmpty()) {
            return;
        }
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_REQUEST_ID, currentRequest.getRequestId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_ID, currentRequest.getPublisherId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_NAME, targetName);
        intent.putExtra(ChatActivity.EXTRA_TARGET_USER_ID, targetUserId);
        intent.putExtra(ChatActivity.EXTRA_REQUEST_STATUS, currentRequest.getStatus());
        intent.putExtra(
                ChatActivity.EXTRA_PARTICIPATION_STATUS,
                participationStatus == null ? ChatActivity.PARTICIPATION_STATUS_NONE : participationStatus
        );
        startActivity(intent);
    }

    private String getGenderRequirementLabel(int requirement) {
        switch (requirement) {
            case 1:
                return "仅男";
            case 2:
                return "仅女";
            default:
                return "不限";
        }
    }
}
