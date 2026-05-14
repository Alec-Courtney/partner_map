package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.CategoryHelper;
import com.androidcourse.partner_map.util.TimeUtil;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<PartnerRequest> data;
    private final boolean isMyRequest;
    private final OnActionListener listener;

    public interface OnActionListener {
        void onView(PartnerRequest request);
        void onEdit(PartnerRequest request);
        void onCancel(PartnerRequest request);
        void onComplete(PartnerRequest request);
    }

    public HistoryAdapter(List<PartnerRequest> data, boolean isMyRequest, OnActionListener listener) {
        this.data = data;
        this.isMyRequest = isMyRequest;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartnerRequest item = data.get(position);
        holder.ivCategory.setImageResource(CategoryHelper.getIconRes(item.getCategory()));
        holder.tvTitle.setText(item.getTitle());

        String statusLabel = CategoryHelper.getStatusLabel(item.getStatus());
        holder.tvStatus.setText(statusLabel);
        int statusDrawable;
        switch (item.getStatus()) {
            case 0:
                statusDrawable = R.drawable.bg_status_recruiting;
                break;
            case 1:
                statusDrawable = R.drawable.bg_status_full;
                break;
            default:
                statusDrawable = R.drawable.bg_status_ended;
                break;
        }
        holder.tvStatus.setBackgroundResource(statusDrawable);

        holder.tvTime.setText(TimeUtil.formatDateTime(item.getScheduledTime()));
        holder.tvPeople.setText(item.getCurrentParticipants() + "/" + item.getMaxParticipants());

        if (isMyRequest) {
            holder.btnAction1.setVisibility(View.VISIBLE);
            holder.btnAction2.setVisibility(View.VISIBLE);
            if (item.getStatus() == 0) {
                holder.btnAction1.setText("编辑");
                holder.btnAction2.setText("取消");
                holder.btnAction1.setOnClickListener(v -> listener.onEdit(item));
                holder.btnAction2.setOnClickListener(v -> listener.onCancel(item));
            } else if (item.getStatus() == 1) {
                holder.btnAction1.setText("查看");
                holder.btnAction2.setText("标记完成");
                holder.btnAction1.setOnClickListener(v -> listener.onView(item));
                holder.btnAction2.setOnClickListener(v -> listener.onComplete(item));
            } else {
                holder.btnAction1.setText("查看");
                holder.btnAction2.setVisibility(View.GONE);
                holder.btnAction1.setOnClickListener(v -> listener.onView(item));
            }
        } else {
            holder.btnAction1.setVisibility(View.GONE);
            holder.btnAction2.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onView(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvTitle, tvStatus, tvTime, tvPeople;
        com.google.android.material.button.MaterialButton btnAction1, btnAction2;

        ViewHolder(View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.iv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPeople = itemView.findViewById(R.id.tv_people);
            btnAction1 = itemView.findViewById(R.id.btn_action1);
            btnAction2 = itemView.findViewById(R.id.btn_action2);
        }
    }
}