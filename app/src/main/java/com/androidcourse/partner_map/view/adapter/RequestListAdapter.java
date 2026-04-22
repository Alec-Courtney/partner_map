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

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.ViewHolder> {
    private final List<PartnerRequest> data;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PartnerRequest request);
    }

    public RequestListAdapter(List<PartnerRequest> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartnerRequest item = data.get(position);
        holder.ivCategory.setImageResource(CategoryHelper.getIconRes(item.getCategory()));
        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText(String.format("%.2fkm · %s", 0.5, CategoryHelper.getStatusLabel(item.getStatus())));
        holder.tvTime.setText(TimeUtil.formatRelative(item.getScheduledTime()) + " · 余" + (item.getMaxParticipants() - item.getCurrentParticipants()) + "人");
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvTitle, tvLocation, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.iv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
