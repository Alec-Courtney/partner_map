package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.User;
import com.androidcourse.partner_map.viewmodel.ProfileViewModel;
import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextView tvNickname, tvSchool, tvAttendRate, tvPraiseRate;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        MaterialToolbar toolbar = findViewById(R.id.iv_back);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvSchool = findViewById(R.id.tv_school);
        tvAttendRate = findViewById(R.id.tv_attend_rate);
        tvPraiseRate = findViewById(R.id.tv_praise_rate);
        LinearLayout layoutMyRequests = findViewById(R.id.layout_my_requests);
        LinearLayout layoutMyParticipations = findViewById(R.id.layout_my_participations);
        LinearLayout layoutMyChats = findViewById(R.id.layout_my_chats);
        LinearLayout layoutEvaluations = findViewById(R.id.layout_evaluations);
        LinearLayout layoutLogout = findViewById(R.id.layout_logout);

        toolbar.setNavigationOnClickListener(v -> finish());
        layoutMyRequests.setOnClickListener(v -> startActivity(new Intent(this, MyRequestsActivity.class)));
        layoutMyParticipations.setOnClickListener(v -> startActivity(new Intent(this, MyParticipationsActivity.class)));
        layoutMyChats.setOnClickListener(v -> startActivity(new Intent(this, MyChatsActivity.class)));
        layoutEvaluations.setOnClickListener(v -> startActivity(new Intent(this, EvaluationActivity.class)));
        layoutLogout.setOnClickListener(v -> logout());

        observeUser();
    }

    private void observeUser() {
        viewModel.getUser().observe(this, this::bindUser);
    }

    private void bindUser(User user) {
        if (user == null) return;
        tvNickname.setText(user.getNickname());
        String schoolLine = user.getSchoolName();
        if (user.getPublishCount() > 0 || user.getParticipateCount() > 0) {
            schoolLine = schoolLine + " · 发布" + user.getPublishCount() + " · 参与" + user.getParticipateCount();
        }
        tvSchool.setText(schoolLine);
        tvAttendRate.setText((int)(user.getAttendRate() * 100) + "%");
        tvPraiseRate.setText((int)(user.getPraiseRate() * 100) + "%");
        if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
            Glide.with(this).load(user.getAvatar()).circleCrop().into(ivAvatar);
        }
    }

    private void logout() {
        viewModel.logout();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
