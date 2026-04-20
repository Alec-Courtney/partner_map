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

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";
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
        String publisherId = getIntent().getStringExtra(EXTRA_PUBLISHER_ID);
        String publisherName = getIntent().getStringExtra(EXTRA_PUBLISHER_NAME);
        currentUserId = SharedPreferencesUtil.getInstance(this).getString("user_id", "");
        isPublisher = currentUserId.equals(publisherId);
        roomId = requestId + "_" + currentUserId; // simplified

        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        rvMessages = findViewById(R.id.rv_messages);
        etInput = findViewById(R.id.et_input);
        Button btnSend = findViewById(R.id.btn_send);

        tvTitle.setText("与 " + publisherName + " 的对话");
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatMessageAdapter(messages, currentUserId, isPublisher);
        rvMessages.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> sendMessage());

        loadMessages();
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
