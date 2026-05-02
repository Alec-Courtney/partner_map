package com.androidcourse.partner_map.view.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.Category;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.CategoryHelper;
import com.androidcourse.partner_map.util.LocationHelper;
import com.androidcourse.partner_map.util.TimeUtil;
import com.androidcourse.partner_map.view.activity.RequestDetailActivity;
import com.androidcourse.partner_map.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {
    private MapViewModel viewModel;
    private MapView mapView;
    private AMap aMap;
    private LinearLayout cardPreview;
    private TextView tvCardTitle, tvCardLocation, tvCardTime;
    private List<PartnerRequest> currentRequests = new ArrayList<>();
    private PartnerRequest selectedRequest;
    private Integer filterCategory;
    private String filterSchoolId;
    private int filterRadius = 10000;
    private String filterTime;
    private double currentLat = 39.9;
    private double currentLng = 116.4;
    private final Map<Marker, PartnerRequest> markerRequestMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        cardPreview = view.findViewById(R.id.card_preview);
        tvCardTitle = view.findViewById(R.id.tv_card_title);
        tvCardLocation = view.findViewById(R.id.tv_card_location);
        tvCardTime = view.findViewById(R.id.tv_card_time);

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        mapView.onCreate(savedInstanceState);
        initMap();
        getCurrentLocation();

        cardPreview.setOnClickListener(v -> {
            if (selectedRequest != null) {
                Intent intent = new Intent(requireContext(), RequestDetailActivity.class);
                intent.putExtra(RequestDetailActivity.EXTRA_REQUEST_ID, selectedRequest.getRequestId());
                startActivity(intent);
            }
        });

        return view;
    }

    private void initMap() {
        aMap = mapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);

        aMap.setOnMarkerClickListener(marker -> {
            PartnerRequest request = markerRequestMap.get(marker);
            if (request != null) {
                selectRequest(request);
            }
            return true;
        });

        aMap.setOnMapLoadedListener(() -> loadRequests());
    }

    private void getCurrentLocation() {
        new LocationHelper(requireContext()).getCurrentLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng) {
                currentLat = lat;
                currentLng = lng;
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(lat, lng)));
                loadRequests();
            }

            @Override
            public void onLocationError(String error) {
                loadRequests();
            }
        });
    }

    private void loadRequests() {
        viewModel.loadRequests(currentLat, currentLng, filterRadius, filterCategory, filterSchoolId, filterTime)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                        currentRequests = resource.data;
                        viewModel.setRequestList(currentRequests);
                        updateMarkers();
                        if (!currentRequests.isEmpty()) {
                            selectRequest(currentRequests.get(0));
                        }
                    }
                });
    }

    private void updateMarkers() {
        aMap.clear();
        markerRequestMap.clear();
        for (PartnerRequest req : currentRequests) {
            LatLng position = new LatLng(req.getRequestLat(), req.getRequestLng());
            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .title(req.getTitle())
                    .snippet(CategoryHelper.getLabel(req.getCategory()));
            try {
                options.icon(BitmapDescriptorFactory.defaultMarker(getHueForCategory(req.getCategory())));
            } catch (Exception e) {
                options.icon(BitmapDescriptorFactory.defaultMarker());
            }
            Marker marker = aMap.addMarker(options);
            markerRequestMap.put(marker, req);
        }
    }

    private float getHueForCategory(int category) {
        switch (category) {
            case 0: return 210f;
            case 1: return 120f;
            case 2: return 30f;
            case 3: return 280f;
            case 4: return 340f;
            case 5: return 50f;
            default: return 0f;
        }
    }

    private void selectRequest(PartnerRequest request) {
        selectedRequest = request;
        tvCardTitle.setText(CategoryHelper.getLabel(request.getCategory()) + " " + request.getTitle());
        float[] results = new float[1];
        android.location.Location.distanceBetween(currentLat, currentLng,
                request.getRequestLat(), request.getRequestLng(), results);
        String dist = results[0] < 1000 ? String.format("%.0fm", results[0])
                : String.format("%.1fkm", results[0] / 1000f);
        tvCardLocation.setText(dist + " · " + CategoryHelper.getStatusLabel(request.getStatus()));
        tvCardTime.setText(TimeUtil.formatRelative(request.getScheduledTime())
                + " · 余" + (request.getMaxParticipants() - request.getCurrentParticipants()) + "人");
        cardPreview.setVisibility(View.VISIBLE);
    }

    public void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(view);
        builder.setTitle("筛选");
        AlertDialog dialog = builder.create();

        int[] selectedCategoryIdx = {-1};
        int[] selectedRadiusIdx = {3};
        int[] selectedTimeIdx = {3};
        int[] selectedSchoolIdx = {0};

        View btnAllCat = view.findViewById(R.id.btn_cat_all);
        View btnStudy = view.findViewById(R.id.btn_cat_study);
        View btnSport = view.findViewById(R.id.btn_cat_sport);
        View btnFood = view.findViewById(R.id.btn_cat_food);
        View btnTravel = view.findViewById(R.id.btn_cat_travel);
        View btnFun = view.findViewById(R.id.btn_cat_fun);
        View btnShopping = view.findViewById(R.id.btn_cat_shopping);

        View.OnClickListener catClick = v -> {
            resetCategoryButtons(view);
            v.setSelected(true);
            v.setBackgroundColor(Color.parseColor("#2196F3"));
            int id = v.getId();
            if (id == R.id.btn_cat_all) selectedCategoryIdx[0] = -1;
            else if (id == R.id.btn_cat_study) selectedCategoryIdx[0] = 0;
            else if (id == R.id.btn_cat_sport) selectedCategoryIdx[0] = 1;
            else if (id == R.id.btn_cat_food) selectedCategoryIdx[0] = 2;
            else if (id == R.id.btn_cat_travel) selectedCategoryIdx[0] = 3;
            else if (id == R.id.btn_cat_fun) selectedCategoryIdx[0] = 4;
            else if (id == R.id.btn_cat_shopping) selectedCategoryIdx[0] = 5;
        };

        if (btnAllCat != null) btnAllCat.setOnClickListener(catClick);
        if (btnStudy != null) btnStudy.setOnClickListener(catClick);
        if (btnSport != null) btnSport.setOnClickListener(catClick);
        if (btnFood != null) btnFood.setOnClickListener(catClick);
        if (btnTravel != null) btnTravel.setOnClickListener(catClick);
        if (btnFun != null) btnFun.setOnClickListener(catClick);
        if (btnShopping != null) btnShopping.setOnClickListener(catClick);

        View btnAllSchool = view.findViewById(R.id.btn_school_all);
        View btnMySchool = view.findViewById(R.id.btn_school_mine);
        View.OnClickListener schoolClick = v -> {
            if (btnAllSchool != null) btnAllSchool.setSelected(false);
            if (btnMySchool != null) btnMySchool.setSelected(false);
            v.setSelected(true);
            selectedSchoolIdx[0] = (v.getId() == R.id.btn_school_mine) ? 1 : 0;
        };
        if (btnAllSchool != null) btnAllSchool.setOnClickListener(schoolClick);
        if (btnMySchool != null) btnMySchool.setOnClickListener(schoolClick);

        View btn5km = view.findViewById(R.id.btn_dist_5);
        View btn10km = view.findViewById(R.id.btn_dist_10);
        View btn20km = view.findViewById(R.id.btn_dist_20);
        View btnDistAll = view.findViewById(R.id.btn_dist_all);
        View.OnClickListener distClick = v -> {
            resetDistButtons(view);
            v.setSelected(true);
            v.setBackgroundColor(Color.parseColor("#2196F3"));
            int id = v.getId();
            if (id == R.id.btn_dist_5) { selectedRadiusIdx[0] = 0; }
            else if (id == R.id.btn_dist_10) { selectedRadiusIdx[0] = 1; }
            else if (id == R.id.btn_dist_20) { selectedRadiusIdx[0] = 2; }
            else { selectedRadiusIdx[0] = 3; }
        };
        if (btn5km != null) btn5km.setOnClickListener(distClick);
        if (btn10km != null) btn10km.setOnClickListener(distClick);
        if (btn20km != null) btn20km.setOnClickListener(distClick);
        if (btnDistAll != null) btnDistAll.setOnClickListener(distClick);

        View btnToday = view.findViewById(R.id.btn_time_today);
        View btnWeek = view.findViewById(R.id.btn_time_week);
        View btnMonth = view.findViewById(R.id.btn_time_month);
        View btnTimeAll = view.findViewById(R.id.btn_time_all);
        View.OnClickListener timeClick = v -> {
            resetTimeButtons(view);
            v.setSelected(true);
            v.setBackgroundColor(Color.parseColor("#2196F3"));
            int id = v.getId();
            if (id == R.id.btn_time_today) selectedTimeIdx[0] = 0;
            else if (id == R.id.btn_time_week) selectedTimeIdx[0] = 1;
            else if (id == R.id.btn_time_month) selectedTimeIdx[0] = 2;
            else selectedTimeIdx[0] = 3;
        };
        if (btnToday != null) btnToday.setOnClickListener(timeClick);
        if (btnWeek != null) btnWeek.setOnClickListener(timeClick);
        if (btnMonth != null) btnMonth.setOnClickListener(timeClick);
        if (btnTimeAll != null) btnTimeAll.setOnClickListener(timeClick);

        view.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            filterCategory = null;
            filterSchoolId = null;
            filterRadius = 10000;
            filterTime = null;
            loadRequests();
            dialog.dismiss();
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            int[] radiusValues = {5000, 10000, 20000, 100000};
            filterRadius = radiusValues[selectedRadiusIdx[0]];
            filterCategory = selectedCategoryIdx[0] >= 0 ? selectedCategoryIdx[0] : null;
            filterTime = selectedTimeIdx[0] == 0 ? "today" : selectedTimeIdx[0] == 1 ? "week" : selectedTimeIdx[0] == 2 ? "month" : null;
            loadRequests();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void resetCategoryButtons(View root) {
        int[] ids = {R.id.btn_cat_all, R.id.btn_cat_study, R.id.btn_cat_sport, R.id.btn_cat_food,
                R.id.btn_cat_travel, R.id.btn_cat_fun, R.id.btn_cat_shopping};
        for (int id : ids) {
            View v = root.findViewById(id);
            if (v != null) {
                v.setSelected(false);
                v.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }
        }
    }

    private void resetDistButtons(View root) {
        int[] ids = {R.id.btn_dist_5, R.id.btn_dist_10, R.id.btn_dist_20, R.id.btn_dist_all};
        for (int id : ids) {
            View v = root.findViewById(id);
            if (v != null) {
                v.setSelected(false);
                v.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }
        }
    }

    private void resetTimeButtons(View root) {
        int[] ids = {R.id.btn_time_today, R.id.btn_time_week, R.id.btn_time_month, R.id.btn_time_all};
        for (int id : ids) {
            View v = root.findViewById(id);
            if (v != null) {
                v.setSelected(false);
                v.setBackgroundColor(Color.parseColor("#E0E0E0"));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) mapView.onDestroy();
    }
}
