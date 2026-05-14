package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.ChatRoom;
import com.androidcourse.partner_map.util.TimeUtil;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    private final List<ChatRoom> data;
    private final String currentUserId;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ChatRoom room);
    }

    public ChatRoomAdapter(List<ChatRoom> data, String currentUserId, OnItemClickListener listener) {
        this.data = data;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoom room = data.get(position);
        holder.tvName.setText(resolveOtherName(room));
        holder.tvRequestTitle.setText(room.getRequestTitle().isEmpty() ? "私聊会话" : room.getRequestTitle());
        holder.tvLastMessage.setText(room.getLastMessage().isEmpty() ? "还没有消息" : room.getLastMessage());
        holder.tvTime.setText(room.getLastMessageAt() > 0 ? TimeUtil.formatRelative(room.getLastMessageAt()) : "");
        holder.itemView.setOnClickListener(v -> listener.onItemClick(room));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private String resolveOtherName(ChatRoom room) {
        if (room == null) {
            return "私聊";
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
        return "私聊";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvRequestTitle;
        TextView tvLastMessage;
        TextView tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvRequestTitle = itemView.findViewById(R.id.tv_request_title);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}