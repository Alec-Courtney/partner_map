package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.ChatMessage;
import com.androidcourse.partner_map.util.TimeUtil;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    private final List<ChatMessage> data;
    private final String currentUserId;
    private final boolean isPublisher;

    public ChatMessageAdapter(List<ChatMessage> data, String currentUserId, boolean isPublisher) {
        this.data = data;
        this.currentUserId = currentUserId;
        this.isPublisher = isPublisher;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getSenderId().equals(currentUserId) ? TYPE_RIGHT : TYPE_LEFT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_right, parent, false);
            return new RightViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message_left, parent, false);
            return new LeftViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = data.get(position);
        if (holder instanceof RightViewHolder) {
            ((RightViewHolder) holder).tvContent.setText(msg.getContent());
            ((RightViewHolder) holder).tvTime.setText(TimeUtil.formatTime(msg.getTimestamp()));
        } else {
            ((LeftViewHolder) holder).tvContent.setText(msg.getContent());
            ((LeftViewHolder) holder).tvTime.setText(TimeUtil.formatTime(msg.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;

        LeftViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }

    static class RightViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;

        RightViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}