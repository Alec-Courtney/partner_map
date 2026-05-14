package com.androidcourse.partner_map.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.androidcourse.partner_map.model.ChatEvent;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.view.adapter.ChatMessageAdapter;
import com.androidcourse.partner_map.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";
    public static final String EXTRA_CHAT_ROOM_ID = "chat_room_id";
    public static final String EXTRA_PUBLISHER_ID = "publisher_id";
    public static final String EXTRA_PUBLISHER_NAME = "publisher_name";
    public static final String EXTRA_TARGET_USER_ID = "target_user_id";
    public static final String EXTRA_REQUEST_STATUS = "request_status";
    public static final String EXTRA_PARTICIPATION_STATUS = "participation_status";
    public static final int PARTICIPATION_STATUS_NONE = -100;

    private ChatViewModel viewModel;
    private RecyclerView rvMessages;
    private EditText etInput;
    private TextView tvTitle;
    private TextView tvRequestStatus;
    private LinearLayout layoutRequestAction;
    private MaterialButton btnApplyJoin;
    private ChatMessageAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private String roomId;
    private String requestId;
    private String publisherId;
    private String publisherName;
    private String targetUserId;
    private String currentUserId;
    private int requestStatus;
    private Integer participationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        roomId = getIntent().getStringExtra(EXTRA_CHAT_ROOM_ID);
        publisherId = getIntent().getStringExtra(EXTRA_PUBLISHER_ID);
        publisherName = getIntent().getStringExtra(EXTRA_PUBLISHER_NAME);
        targetUserId = getIntent().getStringExtra(EXTRA_TARGET_USER_ID);
        requestStatus = getIntent().getIntExtra(EXTRA_REQUEST_STATUS, 0);
        int rawParticipationStatus = getIntent().getIntExtra(EXTRA_PARTICIPATION_STATUS, PARTICIPATION_STATUS_NONE);
        participationStatus = rawParticipationStatus == PARTICIPATION_STATUS_NONE ? null : rawParticipationStatus;
        currentUserId = SharedPreferencesUtil.getInstance(this).getString(Constants.KEY_USER_ID, "");

        bindViews();
        bindActions();
        observeSocketMessages();
        observeChatEvents();
        updateRequestActionState();
        refreshRequestState();

        if (roomId != null && !roomId.isEmpty()) {
            loadMessages();
        } else if (requestId != null && !requestId.isEmpty()) {
            resolveRoomAndEnter();
        } else {
            Toast.makeText(this, "缺少聊天室信息", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bindViews() {
        MaterialToolbar toolbar = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvRequestStatus = findViewById(R.id.tv_request_status);
        layoutRequestAction = findViewById(R.id.layout_request_action);
        btnApplyJoin = findViewById(R.id.btn_apply_join);
        rvMessages = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        MaterialButton btnSend = findViewById(R.id.btn_send);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendMessage());
        btnApplyJoin.setOnClickListener(v -> applyJoin());

        tvTitle.setText(publisherName == null || publisherName.isEmpty() ? "私聊" : publisherName);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatMessageAdapter(messages, currentUserId, false);
        rvMessages.setAdapter(adapter);
    }

    private void bindActions() {
    }

    private void refreshRequestState() {
        if (requestId == null || requestId.isEmpty()) {
            return;
        }
        viewModel.loadRequestDetail(requestId).observe(this, resource -> {
            if (resource.status != com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS || resource.data == null) {
                return;
            }
            bindRequestState(resource.data);
        });
    }

    private void bindRequestState(PartnerRequest request) {
        requestStatus = request.getStatus();
        publisherId = request.getPublisherId();
        if (!currentUserId.equals(request.getPublisherId())
                && !request.getPublisherName().isEmpty()
                && (publisherName == null || publisherName.isEmpty())) {
            publisherName = request.getPublisherName();
        }
        if (!request.isPublisher()) {
            participationStatus = request.getMyParticipationStatus();
        }
        updateRequestActionState();
    }

    private void observeSocketMessages() {
        viewModel.getNewMessage().observe(this, message -> {
            if (message == null || roomId == null || !roomId.equals(message.getChatRoomId())) {
                return;
            }
            messages.add(message);
            adapter.notifyItemInserted(messages.size() - 1);
            rvMessages.scrollToPosition(messages.size() - 1);
        });
    }

    private void observeChatEvents() {
        viewModel.getChatEvent().observe(this, event -> {
            if (event == null) {
                return;
            }
            boolean sameRoom = roomId != null && roomId.equals(event.getChatRoomId());
            boolean sameRequest = requestId != null && requestId.equals(event.getRequestId());
            if (!sameRoom && !sameRequest) {
                return;
            }
            switch (event.getType()) {
                case "PARTICIPATION_APPROVED":
                    participationStatus = 1;
                    Toast.makeText(this, "发起者已同意你的加入申请", Toast.LENGTH_SHORT).show();
                    break;
                case "PARTICIPATION_REJECTED":
                    participationStatus = 2;
                    Toast.makeText(this, "发起者拒绝了你的加入申请", Toast.LENGTH_SHORT).show();
                    break;
                case "REQUEST_COMPLETED":
                case "EVALUATION_REQUIRED":
                    requestStatus = 2;
                    break;
                default:
                    return;
            }
            updateRequestActionState();
        });
    }

    private void resolveRoomAndEnter() {
        viewModel.loadChatRooms().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                for (ChatRoom room : resource.data) {
                    if (!requestId.equals(room.getRequestId())) {
                        continue;
                    }
                    if (!matchesTargetUser(room)) {
                        continue;
                    }
                    roomId = room.getChatRoomId();
                    updateTitleFromRoom(room);
                    loadMessages();
                    return;
                }
                if (currentUserId.equals(publisherId)) {
                    Toast.makeText(this, "对方暂未开启该私聊", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                openPrivateChatRoom();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                if (currentUserId.equals(publisherId)) {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    openPrivateChatRoom();
                }
            }
        });
    }

    private boolean matchesTargetUser(ChatRoom room) {
        boolean isMember = currentUserId.equals(room.getRequesterId()) || currentUserId.equals(room.getPublisherId());
        if (!isMember) {
            return false;
        }
        if (targetUserId == null || targetUserId.isEmpty()) {
            return true;
        }
        if (targetUserId.equals(room.getRequesterId()) || targetUserId.equals(room.getPublisherId())) {
            return true;
        }
        return targetUserId.equals(resolveOtherUserId(room));
    }

    private void openPrivateChatRoom() {
        viewModel.openChatRoom(requestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                roomId = resource.data.getChatRoomId();
                updateTitleFromRoom(resource.data);
                loadMessages();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateTitleFromRoom(ChatRoom room) {
        String name = resolveOtherUserName(room);
        if (name != null && !name.isEmpty()) {
            publisherName = name;
            tvTitle.setText(name);
        }
    }

    private String resolveOtherUserId(ChatRoom room) {
        if (room == null) {
            return null;
        }
        if (currentUserId.equals(room.getPublisherId()) && room.getRequesterId() != null && !room.getRequesterId().isEmpty()) {
            return room.getRequesterId();
        }
        if (currentUserId.equals(room.getRequesterId()) && room.getPublisherId() != null && !room.getPublisherId().isEmpty()) {
            return room.getPublisherId();
        }
        if (room.getPublisherId() != null && !room.getPublisherId().isEmpty() && !currentUserId.equals(room.getPublisherId())) {
            return room.getPublisherId();
        }
        return room.getRequesterId();
    }

    private String resolveOtherUserName(ChatRoom room) {
        if (room == null) {
            return publisherName;
        }
        if (currentUserId.equals(room.getPublisherId()) && room.getRequesterName() != null && !room.getRequesterName().isEmpty()) {
            return room.getRequesterName();
        }
        if (currentUserId.equals(room.getRequesterId()) && room.getPublisherName() != null && !room.getPublisherName().isEmpty()) {
            return room.getPublisherName();
        }
        if (room.getPublisherName() != null && !room.getPublisherName().isEmpty() && !currentUserId.equals(room.getPublisherId())) {
            return room.getPublisherName();
        }
        if (room.getRequesterName() != null && !room.getRequesterName().isEmpty()) {
            return room.getRequesterName();
        }
        return publisherName;
    }

    private void loadMessages() {
        viewModel.loadMessages(roomId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                messages.clear();
                messages.addAll(resource.data);
                adapter.notifyDataSetChanged();
                if (!messages.isEmpty()) {
                    rvMessages.scrollToPosition(messages.size() - 1);
                }
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }
        if (roomId == null || roomId.isEmpty()) {
            Toast.makeText(this, "聊天室尚未准备完成", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.sendMessage(roomId, content).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                messages.add(resource.data);
                adapter.notifyItemInserted(messages.size() - 1);
                rvMessages.scrollToPosition(messages.size() - 1);
                etInput.setText("");
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyJoin() {
        if (requestId == null || requestId.isEmpty()) {
            return;
        }
        viewModel.participate(requestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                Object chatRoomId = resource.data.get("chatRoomId");
                if (roomId == null && chatRoomId != null) {
                    roomId = chatRoomId.toString();
                    loadMessages();
                }
                participationStatus = 0;
                Toast.makeText(this, "加入申请已提交，等待发起者审核", Toast.LENGTH_SHORT).show();
                updateRequestActionState();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRequestActionState() {
        if (requestId == null || requestId.isEmpty() || currentUserId.equals(publisherId)) {
            layoutRequestAction.setVisibility(View.GONE);
            return;
        }

        layoutRequestAction.setVisibility(View.VISIBLE);
        btnApplyJoin.setVisibility(View.GONE);
        btnApplyJoin.setEnabled(false);

        if (participationStatus == null) {
            if (requestStatus == 0) {
                tvRequestStatus.setText("当前只是私聊，还没有加入该需求");
                btnApplyJoin.setVisibility(View.VISIBLE);
                btnApplyJoin.setEnabled(true);
            } else if (requestStatus == 1) {
                tvRequestStatus.setText("需求已满员，无法再申请加入");
            } else {
                tvRequestStatus.setText("需求已结束");
            }
            return;
        }

        if (participationStatus == 0) {
            tvRequestStatus.setText("已提交加入申请，等待发起者审核");
        } else if (participationStatus == 1) {
            tvRequestStatus.setText("你已加入该需求，可以继续沟通");
        } else {
            tvRequestStatus.setText("加入申请已被拒绝，可继续私聊沟通");
        }
    }
}
