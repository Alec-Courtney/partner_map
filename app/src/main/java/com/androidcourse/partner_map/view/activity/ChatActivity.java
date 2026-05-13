package com.androidcourse.partner_map.view.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.view.adapter.ChatMessageAdapter;
import com.androidcourse.partner_map.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";
    public static final String EXTRA_CHAT_ROOM_ID = "chat_room_id";
    public static final String EXTRA_PUBLISHER_ID = "publisher_id";
    public static final String EXTRA_PUBLISHER_NAME = "publisher_name";

    private ChatViewModel viewModel;
    private RecyclerView rvMessages;
    private EditText etInput;
    private ChatMessageAdapter adapter;
    private final List<ChatMessage> messages = new ArrayList<>();
    private String roomId;
    private String currentUserId;
    private boolean isPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        String requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        String chatRoomId = getIntent().getStringExtra(EXTRA_CHAT_ROOM_ID);
        String publisherId = getIntent().getStringExtra(EXTRA_PUBLISHER_ID);
        String publisherName = getIntent().getStringExtra(EXTRA_PUBLISHER_NAME);
        currentUserId = SharedPreferencesUtil.getInstance(this).getString("user_id", "");
        isPublisher = currentUserId.equals(publisherId);

        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        rvMessages = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        Button btnSend = findViewById(R.id.btn_send);

        String displayTitle = publisherName != null ? publisherName : "对方";
        tvTitle.setText("与 " + displayTitle + " 的对话");
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatMessageAdapter(messages, currentUserId, isPublisher);
        rvMessages.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendMessage());
        viewModel.getNewMessage().observe(this, message -> {
            if (message == null || roomId == null) {
                return;
            }
            if (!roomId.equals(message.getChatRoomId())) {
                return;
            }
            messages.add(message);
            adapter.notifyItemInserted(messages.size() - 1);
            rvMessages.scrollToPosition(messages.size() - 1);
        });

        if (chatRoomId != null && !chatRoomId.isEmpty()) {
            roomId = chatRoomId;
            loadMessages();
        } else if (requestId != null) {
            joinChatRoom(requestId);
        }
    }

    private void joinChatRoom(String requestId) {
        viewModel.participate(requestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS
                    && resource.data != null) {
                Object chatRoomIdObj = resource.data.get("chatRoomId");
                roomId = chatRoomIdObj != null ? chatRoomIdObj.toString() : (requestId + "_" + currentUserId);
                loadMessages();
            } else {
                Toast.makeText(this, "加入聊天失败: " + resource.message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadMessages() {
        viewModel.loadMessages(roomId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                messages.clear();
                messages.addAll(resource.data);
                adapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage() {
        String content = etInput.getText().toString().trim();
        if (content.isEmpty()) return;
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
}
