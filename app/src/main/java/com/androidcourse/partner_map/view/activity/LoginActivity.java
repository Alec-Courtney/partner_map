package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.app.Constants;
import com.androidcourse.partner_map.data.remote.ApiClient;
import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.util.SharedPreferencesUtil;
import com.androidcourse.partner_map.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private EditText etNickname, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        if (viewModel.isLoggedIn()) {
            restoreTokenAndStartMain();
            return;
        }

        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> doLogin());
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void restoreTokenAndStartMain() {
        String token = SharedPreferencesUtil.getInstance(this).getString(Constants.KEY_TOKEN, null);
        if (token != null && !token.isEmpty()) {
            WebSocketManager.getInstance().connect(token);
        }
        startMain();
    }

    private void doLogin() {
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入昵称和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.login(nickname, password).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                WebSocketManager.getInstance().connect(resource.data.getToken());
                startMain();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
