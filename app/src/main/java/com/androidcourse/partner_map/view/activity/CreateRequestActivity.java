package com.androidcourse.partner_map.view.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.LocationHelper;
import com.androidcourse.partner_map.util.TimeUtil;
import com.androidcourse.partner_map.view.widget.CategoryPicker;
import com.androidcourse.partner_map.viewmodel.CreateRequestViewModel;

import java.util.Calendar;
import java.util.Locale;

public class CreateRequestActivity extends AppCompatActivity {
    public static final String EXTRA_REQUEST_ID = "request_id";

    private CreateRequestViewModel viewModel;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etCost;
    private CategoryPicker categoryPicker;
    private TextView tvRequestLocation;
    private TextView tvScheduledTime;
    private TextView tvExpireTime;
    private TextView tvMaxParticipants;
    private RadioGroup rgGender;
    private Button btnPublish;
    private String editingRequestId;
    private int selectedCategory = -1;
    private double requestLat;
    private double requestLng;
    private double publishLat;
    private double publishLng;
    private boolean publishLocationReady;
    private int expireBeforeMin = 60;
    private int maxParticipants = 1;
    private final Calendar scheduledCalendar = Calendar.getInstance();
    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        scheduledCalendar.add(Calendar.HOUR_OF_DAY, 1);
        editingRequestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        viewModel = new ViewModelProvider(this).get(CreateRequestViewModel.class);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        fetchCurrentLocationForRequest();
                    } else {
                        Toast.makeText(this, "需要定位权限才能选择位置", Toast.LENGTH_LONG).show();
                    }
                }
        );

        bindViews();
        bindActions();

        if (isEditMode()) {
            btnPublish.setText("保存修改");
            loadRequestForEdit();
        } else {
            checkAndFetchPublishLocation();
        }
    }

    private void bindViews() {
        MaterialToolbar toolbar = findViewById(R.id.iv_back);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etCost = findViewById(R.id.et_cost);
        categoryPicker = findViewById(R.id.category_picker);
        tvRequestLocation = findViewById(R.id.tv_request_location);
        tvScheduledTime = findViewById(R.id.tv_scheduled_time);
        tvExpireTime = findViewById(R.id.tv_expire_time);
        tvMaxParticipants = findViewById(R.id.tv_max_participants);
        rgGender = findViewById(R.id.rg_gender);
        btnPublish = findViewById(R.id.btn_publish);

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindActions() {
        findViewById(R.id.iv_minus).setOnClickListener(v -> {
            if (maxParticipants > 1) {
                maxParticipants--;
                tvMaxParticipants.setText(String.valueOf(maxParticipants));
            }
        });
        findViewById(R.id.iv_plus).setOnClickListener(v -> {
            maxParticipants++;
            tvMaxParticipants.setText(String.valueOf(maxParticipants));
        });
        tvScheduledTime.setOnClickListener(v -> pickDateTime());
        tvExpireTime.setOnClickListener(v -> pickExpireTime());
        tvRequestLocation.setOnClickListener(v -> checkLocationAndPick());
        btnPublish.setOnClickListener(v -> submit());
        categoryPicker.setOnCategorySelectedListener(code -> selectedCategory = code);
    }

    private boolean isEditMode() {
        return editingRequestId != null && !editingRequestId.trim().isEmpty();
    }

    private void loadRequestForEdit() {
        viewModel.getRequestDetail(editingRequestId).observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                bindEditableRequest(resource.data);
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void bindEditableRequest(PartnerRequest request) {
        etTitle.setText(request.getTitle());
        etDescription.setText(request.getDescription());
        etCost.setText(request.getCostDescription());
        selectedCategory = request.getCategory();
        categoryPicker.setSelectedCategory(selectedCategory);

        requestLat = request.getRequestLat();
        requestLng = request.getRequestLng();
        updateRequestLocationLabel(requestLat, requestLng);

        publishLat = request.getPublishLat();
        publishLng = request.getPublishLng();
        publishLocationReady = publishLat != 0D || publishLng != 0D;

        maxParticipants = Math.max(request.getMaxParticipants(), 1);
        tvMaxParticipants.setText(String.valueOf(maxParticipants));

        expireBeforeMin = request.getExpireBeforeMin();
        tvExpireTime.setText(getExpireLabel(expireBeforeMin));

        if (request.getScheduledTime() > 0) {
            scheduledCalendar.setTimeInMillis(request.getScheduledTime());
            tvScheduledTime.setText(TimeUtil.formatDateTime(request.getScheduledTime()));
        }

        switch (request.getGenderRequirement()) {
            case 1:
                rgGender.check(R.id.rb_male_only);
                break;
            case 2:
                rgGender.check(R.id.rb_female_only);
                break;
            default:
                rgGender.check(R.id.rb_any);
                break;
        }
    }

    private void checkAndFetchPublishLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchPublishLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要定位权限")
                    .setMessage("发布搭子需求需要获取您的位置信息。")
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
            fetchCurrentLocationForRequest();
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

    private void fetchCurrentLocationForRequest() {
        new LocationHelper(this).getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng) {
                requestLat = lat;
                requestLng = lng;
                updateRequestLocationLabel(lat, lng);
                if (!publishLocationReady) {
                    publishLat = lat;
                    publishLng = lng;
                    publishLocationReady = true;
                }
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(CreateRequestActivity.this, "定位失败: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRequestLocationLabel(double lat, double lng) {
        tvRequestLocation.setText(String.format(Locale.CHINA, "%.6f, %.6f", lat, lng));
    }

    private void pickDateTime() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            scheduledCalendar.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                scheduledCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                scheduledCalendar.set(Calendar.MINUTE, minute);
                scheduledCalendar.set(Calendar.SECOND, 0);
                tvScheduledTime.setText(TimeUtil.formatDateTime(scheduledCalendar.getTimeInMillis()));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pickExpireTime() {
        String[] items = {"30分钟", "1小时", "2小时", "6小时"};
        int[] mins = {30, 60, 120, 360};
        new AlertDialog.Builder(this)
                .setTitle("失效提前量")
                .setItems(items, (dialog, which) -> {
                    expireBeforeMin = mins[which];
                    tvExpireTime.setText(items[which]);
                })
                .show();
    }

    private String getExpireLabel(int minutes) {
        if (minutes < 60) {
            return minutes + "分钟";
        }
        if (minutes % 60 == 0) {
            return (minutes / 60) + "小时";
        }
        return minutes + "分钟";
    }

    private void submit() {
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
        if (requestLat == 0D && requestLng == 0D) {
            Toast.makeText(this, "请选择需求位置", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!publishLocationReady) {
            Toast.makeText(this, "发布位置尚未准备好，请稍后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        if (scheduledCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, "预定时间必须晚于当前时间", Toast.LENGTH_SHORT).show();
            return;
        }

        PartnerRequest request = new PartnerRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategory(selectedCategory);
        request.setRequestLat(requestLat);
        request.setRequestLng(requestLng);
        request.setRequestAddress(tvRequestLocation.getText().toString());
        request.setPublishLat(publishLat);
        request.setPublishLng(publishLng);
        request.setMaxParticipants(maxParticipants);
        request.setScheduledTime(scheduledCalendar.getTimeInMillis());
        request.setExpireBeforeMin(expireBeforeMin);
        request.setGenderRequirement(resolveGenderRequirement());
        request.setCostDescription(etCost.getText().toString().trim());

        androidx.lifecycle.LiveData<com.androidcourse.partner_map.data.repository.Resource<PartnerRequest>> liveData =
                isEditMode()
                        ? viewModel.updateRequest(editingRequestId, request)
                        : viewModel.createRequest(request);

        liveData.observe(this, resource -> {
            if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS) {
                Toast.makeText(this, isEditMode() ? "修改成功" : "发布成功", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.ERROR) {
                Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private int resolveGenderRequirement() {
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_male_only) {
            return 1;
        }
        if (checkedId == R.id.rb_female_only) {
            return 2;
        }
        return 0;
    }
}
