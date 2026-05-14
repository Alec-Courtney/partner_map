package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.data.remote.WebSocketManager;
import com.androidcourse.partner_map.model.School;
import com.androidcourse.partner_map.viewmodel.RegisterViewModel;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;
    private EditText etNickname, etPassword, etConfirmPassword;
    private RadioGroup rgGender;
    private Spinner spinnerSchool;
    private final List<School> schoolList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        etNickname = findViewById(R.id.et_nickname);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        rgGender = findViewById(R.id.rg_gender);
        spinnerSchool = findViewById(R.id.spinner_school);
        Button btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> doRegister());

        loadSchools();
    }

    private void loadSchools() {
        viewModel.getSchools().observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                schoolList.clear();
                schoolList.addAll(resource.data);
                List<String> names = new ArrayList<>();
                names.add("请选择学校");
                for (School s : resource.data) {
                    names.add(s.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSchool.setAdapter(adapter);
            }
        });
    }

    private void doRegister() {
        String nickname = etNickname.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();
        if (nickname.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nickname.length() < 2 || nickname.length() > 20) {
            Toast.makeText(this, "昵称长度需在2到20个字符之间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "密码长度需在6到20个字符之间", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        int genderId = rgGender.getCheckedRadioButtonId();
        int gender = 0;
        if (genderId == R.id.rb_male) gender = 1;
        else if (genderId == R.id.rb_female) gender = 2;

        int schoolPos = spinnerSchool.getSelectedItemPosition();
        if (schoolPos <= 0 || schoolPos > schoolList.size()) {
            Toast.makeText(this, "请选择学校", Toast.LENGTH_SHORT).show();
            return;
        }
        String schoolId = schoolList.get(schoolPos - 1).getSchoolId();

        viewModel.register(nickname, password, gender, schoolId, null).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                String token = resource.data.getToken() != null ? resource.data.getToken() : resource.data.getUserId();
                WebSocketManager.getInstance().connect(token);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
