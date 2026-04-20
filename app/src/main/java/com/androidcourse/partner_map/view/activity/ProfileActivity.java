package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.User;
import com.androidcourse.partner_map.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ProfileViewModel viewModel;
    private TextView tvNickname, tvSchool, tvAttendRate, tvPraiseRate;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvSchool = findViewById(R.id.tv_school);
        tvAttendRate = findViewById(R.id.tv_attend_rate);
        tvPraiseRate = findViewById(R.id.tv_praise_rate);
        LinearLayout layoutMyRequests = findViewById(R.id.layout_my_requests);
        LinearLayout layoutMyParticipations = findViewById(R.id.layout_my_participations);
        LinearLayout layoutEvaluations = findViewById(R.id.layout_evaluations);
        LinearLayout layoutLogout = findViewById(R.id.layout_logout);

        ivBack.setOnClickListener(v -> finish());
        layoutMyRequests.setOnClickListener(v -> startActivity(new Intent(this, MyRequestsActivity.class)));
        layoutMyParticipations.setOnClickListener(v -> startActivity(new Intent(this, MyParticipationsActivity.class)));
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
        tvSchool.setText(user.getSchoolName() != null ? user.getSchoolName() : "");
        tvAttendRate.setText((int)(user.getAttendRate() * 100) + "%");
        tvPraiseRate.setText((int)(user.getPraiseRate() * 100) + "%");
    }

    private void logout() {
        viewModel.logout();
        Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
