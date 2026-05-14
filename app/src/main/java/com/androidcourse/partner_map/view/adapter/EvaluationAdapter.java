package com.androidcourse.partner_map.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Evaluation;

import java.util.List;

public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.ViewHolder> {
    private final List<Evaluation> data;
    private final OnSubmitListener listener;

    public interface OnSubmitListener {
        void onSubmit(Evaluation evaluation);
    }

    public EvaluationAdapter(List<Evaluation> data, OnSubmitListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evaluation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Evaluation ev = data.get(position);
        holder.tvTarget.setText("评价 " + ev.getToUserName());

        holder.btnSubmit.setOnClickListener(v -> {
            int attendId = holder.rgAttend.getCheckedRadioButtonId();
            ev.setAttended(attendId == R.id.rb_attend_yes);

            int praiseId = holder.rgPraise.getCheckedRadioButtonId();
            ev.setPraised(praiseId == R.id.rb_praise_good);

            listener.onSubmit(ev);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTarget;
        RadioGroup rgAttend, rgPraise;
        com.google.android.material.button.MaterialButton btnSubmit;

        ViewHolder(View itemView) {
            super(itemView);
            tvTarget = itemView.findViewById(R.id.tv_target);
            rgAttend = itemView.findViewById(R.id.rg_attend);
            rgPraise = itemView.findViewById(R.id.rg_praise);
            btnSubmit = itemView.findViewById(R.id.btn_submit);
        }
    }
}