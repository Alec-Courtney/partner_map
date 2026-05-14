package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private final List<User> data;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public UserListAdapter(List<User> data) {
        this.data = data;
        this.listener = null;
    }

    public UserListAdapter(List<User> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = data.get(position);
        holder.tvName.setText(user.getNickname());
        holder.tvSchool.setText(user.getSchoolName() != null ? user.getSchoolName() : "");
        holder.tvStatus.setText(String.format("好评率 %.0f%%", user.getPraiseRate() * 100));
        holder.itemView.findViewById(R.id.btn_approve).setVisibility(View.GONE);
        holder.itemView.findViewById(R.id.btn_reject).setVisibility(View.GONE);
        holder.itemView.setOnClickListener(listener == null ? null : v -> listener.onItemClick(user));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSchool, tvStatus;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSchool = itemView.findViewById(R.id.tv_school);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
