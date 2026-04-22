package com.androidcourse.partner_map.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.LocationHelper;
import com.androidcourse.partner_map.view.widget.CategoryPicker;
import com.androidcourse.partner_map.viewmodel.CreateRequestViewModel;

import java.util.Calendar;

public class CreateRequestActivity extends AppCompatActivity {
    private CreateRequestViewModel viewModel;
    private EditText etTitle, etDescription, etCost;
    private CategoryPicker categoryPicker;
    private TextView tvRequestLocation, tvScheduledTime, tvExpireTime;
    private RadioGroup rgGender;
    private int selectedCategory = -1;
    private double requestLat, requestLng;
    private double publishLat, publishLng;
    private Calendar scheduledCalendar = Calendar.getInstance();
    private int expireBeforeMin = 60;
    private int maxParticipants = 2;
    private TextView tvMaxParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        viewModel = new ViewModelProvider(this).get(CreateRequestViewModel.class);

        ImageView ivBack = findViewById(R.id.iv_back);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        categoryPicker = findViewById(R.id.category_picker);
        tvRequestLocation = findViewById(R.id.tv_request_location);
        tvScheduledTime = findViewById(R.id.tv_scheduled_time);
        tvExpireTime = findViewById(R.id.tv_expire_time);
        rgGender = findViewById(R.id.rg_gender);
        etCost = findViewById(R.id.et_cost);
        tvMaxParticipants = findViewById(R.id.tv_max_participants);
        Button btnPublish = findViewById(R.id.btn_publish);
        ImageView ivMinus = findViewById(R.id.iv_minus);
        ImageView ivPlus = findViewById(R.id.iv_plus);

        ivBack.setOnClickListener(v -> finish());
        ivMinus.setOnClickListener(v -> {
            if (maxParticipants > 2) {
                maxParticipants--;
                tvMaxParticipants.setText(String.valueOf(maxParticipants));
            }
        });
        ivPlus.setOnClickListener(v -> {
            maxParticipants++;
            tvMaxParticipants.setText(String.valueOf(maxParticipants));
        });
        tvScheduledTime.setOnClickListener(v -> pickDateTime());
        tvExpireTime.setOnClickListener(v -> pickExpireTime());
        tvRequestLocation.setOnClickListener(v -> pickLocation());
        btnPublish.setOnClickListener(v -> publish());

        categoryPicker.setOnCategorySelectedListener(code -> selectedCategory = code);

        new LocationHelper(this).getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng) {
                publishLat = lat;
                publishLng = lng;
            }
            @Override public void onLocationError(String error) {}
        });
    }

    private void pickDateTime() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            scheduledCalendar.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                scheduledCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                scheduledCalendar.set(Calendar.MINUTE, minute);
                tvScheduledTime.setText(String.format("%d-%02d-%02d %02d:%02d",
                        year, month + 1, dayOfMonth, hourOfDay, minute));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickExpireTime() {
        String[] items = {"30分钟", "1小时", "2小时", "自定义"};
        int[] mins = {30, 60, 120, 60};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("失效提前量")
                .setItems(items, (dialog, which) -> {
                    expireBeforeMin = mins[which];
                    tvExpireTime.setText(items[which]);
                }).show();
    }

    private void pickLocation() {
        // In real app: open map to select point
        // Mock: use current location
        requestLat = publishLat;
        requestLng = publishLng;
        tvRequestLocation.setText("已选择当前位置");
    }

    private void publish() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "请填写标题和描述", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategory < 0) {
            Toast.makeText(this, "请选择分类", Toast.LENGTH_SHORT).show();
            return;
        }

        PartnerRequest req = new PartnerRequest();
        req.setTitle(title);
        req.setDescription(description);
        req.setCategory(selectedCategory);
        req.setRequestLat(requestLat);
        req.setRequestLng(requestLng);
        req.setPublishLat(publishLat);
        req.setPublishLng(publishLng);
        req.setMaxParticipants(maxParticipants);
        req.setScheduledTime(scheduledCalendar.getTimeInMillis());
        req.setExpireBeforeMin(expireBeforeMin);

        int genderId = rgGender.getCheckedRadioButtonId();
        if (genderId == R.id.rb_male_only) req.setGenderRequirement("男");
        else if (genderId == R.id.rb_female_only) req.setGenderRequirement("女");
        else req.setGenderRequirement("不限");

        String cost = etCost.getText().toString().trim();
        req.setCostDescription(cost.isEmpty() ? null : cost);

        viewModel.createRequest(req).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
