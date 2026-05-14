package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Participation;

import java.util.List;

public class ParticipationAdapter extends RecyclerView.Adapter<ParticipationAdapter.ViewHolder> {
    private List<Participation> data;
    private final OnActionListener actionListener;
    private final OnItemClickListener itemListener;

    public interface OnActionListener {
        void onApprove(Participation p);
        void onReject(Participation p);
    }

    public interface OnItemClickListener {
        void onItemClick(Participation p);
    }

    public ParticipationAdapter() {
        this.data = null;
        this.actionListener = null;
        this.itemListener = null;
    }

    public ParticipationAdapter(List<Participation> data, OnActionListener listener) {
        this.data = data;
        this.actionListener = listener;
        this.itemListener = null;
    }

    public ParticipationAdapter(List<Participation> data,
                                 OnActionListener actionListener,
                                 OnItemClickListener itemListener) {
        this.data = data;
        this.actionListener = actionListener;
        this.itemListener = itemListener;
    }

    public ParticipationAdapter(List<Participation> data, OnItemClickListener listener) {
        this.data = data;
        this.actionListener = null;
        this.itemListener = listener;
    }

    public void setData(List<Participation> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participation p = data.get(position);
        holder.tvName.setText(p.getUserName());
        holder.tvSchool.setText(p.getUserSchool() != null ? p.getUserSchool() : "");
        String statusText;
        if (p.getStatus() == 0) statusText = "待审批";
        else if (p.getStatus() == 1) statusText = "已加入";
        else statusText = "已退出";
        holder.tvStatus.setText(statusText);

        if (actionListener != null && p.getStatus() == 0) {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnApprove.setOnClickListener(v -> actionListener.onApprove(p));
            holder.btnReject.setOnClickListener(v -> actionListener.onReject(p));
        } else {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(itemListener == null ? null : v -> itemListener.onItemClick(p));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSchool, tvStatus;
        com.google.android.material.button.MaterialButton btnApprove, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSchool = itemView.findViewById(R.id.tv_school);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnApprove = itemView.findViewById(R.id.btn_approve);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}