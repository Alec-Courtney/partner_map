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
import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.view.adapter.ChatRoomAdapter;
import com.androidcourse.partner_map.viewmodel.MyChatsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyChatsActivity extends AppCompatActivity {
    private MyChatsViewModel viewModel;
    private ChatRoomAdapter adapter;
    private final List<ChatRoom> data = new ArrayList<>();
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

        currentUserId = SharedPreferencesUtil.getInstance(this).getString(Constants.KEY_USER_ID, "");
        viewModel = new ViewModelProvider(this).get(MyChatsViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatRoomAdapter(data, currentUserId, this::openChatRoom);
        recyclerView.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        viewModel.loadChatRooms().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                data.clear();
                data.addAll(resource.data);
                adapter.notifyDataSetChanged();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChatRoom(ChatRoom room) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID, room.getChatRoomId());
        intent.putExtra(ChatActivity.EXTRA_REQUEST_ID, room.getRequestId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_ID, room.getPublisherId());
        intent.putExtra(ChatActivity.EXTRA_PUBLISHER_NAME, resolveOtherName(room));
        intent.putExtra(ChatActivity.EXTRA_TARGET_USER_ID, resolveOtherUserId(room));
        startActivity(intent);
    }

    private String resolveOtherUserId(ChatRoom room) {
        if (currentUserId.equals(room.getPublisherId()) && room.getRequesterId() != null && !room.getRequesterId().isEmpty()) {
            return room.getRequesterId();
        }
        if (room.getPublisherId() != null && !room.getPublisherId().isEmpty()) {
            return room.getPublisherId();
        }
        return room.getRequesterId();
    }

    private String resolveOtherName(ChatRoom room) {
        if (currentUserId.equals(room.getPublisherId()) && room.getRequesterName() != null && !room.getRequesterName().isEmpty()) {
            return room.getRequesterName();
        }
        if (room.getPublisherName() != null && !room.getPublisherName().isEmpty() && !currentUserId.equals(room.getPublisherId())) {
            return room.getPublisherName();
        }
        if (room.getRequesterName() != null && !room.getRequesterName().isEmpty()) {
            return room.getRequesterName();
        }
        return "私聊";
    }
}
