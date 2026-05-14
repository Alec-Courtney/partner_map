package com.androidcourse.partner_map.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.view.fragment.MapFragment;
import com.androidcourse.partner_map.view.fragment.RequestListFragment;
import com.androidcourse.partner_map.viewmodel.MapViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private MapViewModel viewModel;
    private TextView tvTabMap, tvTabList;
    private MapFragment mapFragment;
    private RequestListFragment listFragment;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), granted -> {
                    if (!granted) {
                        Toast.makeText(this, "定位权限未授权，地图功能可能受限", Toast.LENGTH_LONG).show();
                    }
                });

        ImageView ivFilter = findViewById(R.id.iv_filter);
        ImageView ivProfile = findViewById(R.id.iv_profile);
        tvTabMap = findViewById(R.id.tv_tab_map);
        tvTabList = findViewById(R.id.tv_tab_list);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        mapFragment = new MapFragment();
        listFragment = new RequestListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .commit();

        tvTabMap.setOnClickListener(v -> switchMode(true));
        tvTabList.setOnClickListener(v -> switchMode(false));
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, CreateRequestActivity.class)));
        ivProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        ivFilter.setOnClickListener(v -> showFilterForCurrentPage());

        viewModel.getIsMapMode().observe(this, isMap -> {
            tvTabMap.setSelected(isMap);
            tvTabList.setSelected(!isMap);
        });

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("需要定位权限")
                    .setMessage("搭子地图需要获取您的位置信息，用于显示周边需求和地图定位功能。")
                    .setPositiveButton("去授权", (d, w) ->
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                    .setNegativeButton("暂不", null)
                    .show();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void switchMode(boolean isMap) {
        viewModel.getIsMapMode().setValue(isMap);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, isMap ? mapFragment : listFragment)
                .commit();
    }

    private void showFilterForCurrentPage() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(current instanceof MapFragment)) {
            switchMode(true);
            getSupportFragmentManager().executePendingTransactions();
        }
        Fragment active = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (active instanceof MapFragment) {
            ((MapFragment) active).showFilterDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapFragment != null && mapFragment.isAdded()) {
            mapFragment.refreshRequests();
        }
    }
}
