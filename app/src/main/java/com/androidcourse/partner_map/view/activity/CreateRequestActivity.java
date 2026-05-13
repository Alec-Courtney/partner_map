package com.androidcourse.partner_map.view.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.LocationHelper;
import com.androidcourse.partner_map.view.widget.CategoryPicker;
import com.androidcourse.partner_map.viewmodel.CreateRequestViewModel;

import java.util.Calendar;

public class CreateRequestActivity extends AppCompatActivity {
    private static final String TAG = "CreateRequest";

    private CreateRequestViewModel viewModel;
    private EditText etTitle, etDescription, etCost;
    private CategoryPicker categoryPicker;
    private TextView tvRequestLocation, tvScheduledTime, tvExpireTime;
    private RadioGroup rgGender;
    private int selectedCategory = -1;
    private double requestLat, requestLng;
    private double publishLat, publishLng;
    private Calendar scheduledCalendar;
    private int expireBeforeMin = 60;
    private int maxParticipants = 2;
    private TextView tvMaxParticipants;
    private boolean publishLocationReady;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        scheduledCalendar = Calendar.getInstance();
        scheduledCalendar.add(Calendar.HOUR_OF_DAY, 1);

        viewModel = new ViewModelProvider(this).get(CreateRequestViewModel.class);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (granted) {
                        fetchPublishLocation();
                    } else {
                        Toast.makeText(this, "需要定位权限才能发布需求", Toast.LENGTH_LONG).show();
                    }
                });

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
        tvRequestLocation.setOnClickListener(v -> checkLocationAndPick());
        btnPublish.setOnClickListener(v -> publish());

        categoryPicker.setOnCategorySelectedListener(code -> selectedCategory = code);

        checkAndFetchPublishLocation();
    }

    private void checkAndFetchPublishLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchPublishLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要定位权限")
                    .setMessage("发布搭子需求需要获取您的位置信息，用于标记需求地点和发布位置。")
                    .setPositiveButton("去授权", (d, w) -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchPublishLocation() {
        new LocationHelper(this).getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng) {
                publishLat = lat;
                publishLng = lng;
                publishLocationReady = true;
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(CreateRequestActivity.this, "获取发布位置失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationAndPick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            pickLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要定位权限")
                    .setMessage("需要获取您的位置来标记需求地点。")
                    .setPositiveButton("去授权", (d, w) -> locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
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
        new AlertDialog.Builder(this)
                .setTitle("失效提前量")
                .setItems(items, (dialog, which) -> {
                    expireBeforeMin = mins[which];
                    tvExpireTime.setText(items[which]);
                }).show();
    }

    private void pickLocation() {
        new LocationHelper(this).getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng) {
                requestLat = lat;
                requestLng = lng;
                tvRequestLocation.setText(String.format("已选择位置: %.4f, %.4f", lat, lng));
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(CreateRequestActivity.this, "定位失败: " + error + "，请稍后重试", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (requestLat == 0 && requestLng == 0) {
            Toast.makeText(this, "请选择需求位置", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!publishLocationReady) {
            Toast.makeText(this, "正在获取发布位置，请稍候...", Toast.LENGTH_SHORT).show();
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
        if (genderId == R.id.rb_male_only) req.setGenderRequirement(1);
        else if (genderId == R.id.rb_female_only) req.setGenderRequirement(2);
        else req.setGenderRequirement(0);

        String cost = etCost.getText().toString().trim();
        req.setCostDescription(cost.isEmpty() ? null : cost);

        Log.d(TAG, "Publishing request: title=" + title + ", category=" + selectedCategory
                + ", scheduledTime=" + scheduledCalendar.getTimeInMillis()
                + ", requestLat=" + requestLat + ", requestLng=" + requestLng
                + ", publishLat=" + publishLat + ", publishLng=" + publishLng);

        viewModel.createRequest(req).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                String msg = resource.message != null ? resource.message : "发布失败，请重试";
                Log.e(TAG, "Publish failed: " + msg);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
