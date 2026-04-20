package com.androidcourse.partner_map.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);

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
        ivFilter.setOnClickListener(v -> {
            if (mapFragment != null) mapFragment.showFilterDialog();
        });

        viewModel.getIsMapMode().observe(this, isMap -> {
            tvTabMap.setSelected(isMap);
            tvTabList.setSelected(!isMap);
        });
    }

    private void switchMode(boolean isMap) {
        viewModel.getIsMapMode().setValue(isMap);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, isMap ? mapFragment : listFragment)
                .commit();
    }
}
