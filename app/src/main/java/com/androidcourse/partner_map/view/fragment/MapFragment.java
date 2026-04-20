package com.androidcourse.partner_map.view.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.util.CategoryHelper;
import com.androidcourse.partner_map.util.TimeUtil;
import com.androidcourse.partner_map.view.activity.RequestDetailActivity;
import com.androidcourse.partner_map.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private MapViewModel viewModel;
    private FrameLayout mapContainer;
    private LinearLayout cardPreview;
    private TextView tvCardTitle, tvCardLocation, tvCardTime;
    private List<PartnerRequest> currentRequests = new ArrayList<>();
    private PartnerRequest selectedRequest;
    private Integer filterCategory;
    private String filterSchoolId;
    private int filterRadius = 10000;
    private String filterTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapContainer = view.findViewById(R.id.map_container);
        cardPreview = view.findViewById(R.id.card_preview);
        tvCardTitle = view.findViewById(R.id.tv_card_title);
        tvCardLocation = view.findViewById(R.id.tv_card_location);
        tvCardTime = view.findViewById(R.id.tv_card_time);

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);

        mapContainer.addView(new MapPlaceholderView(requireContext()));
        cardPreview.setOnClickListener(v -> {
            if (selectedRequest != null) {
                Intent intent = new Intent(requireContext(), RequestDetailActivity.class);
                intent.putExtra(RequestDetailActivity.EXTRA_REQUEST_ID, selectedRequest.getRequestId());
                startActivity(intent);
            }
        });

        loadRequests();
        return view;
    }

    private void loadRequests() {
        viewModel.loadRequests(39.9, 116.4, filterRadius, filterCategory, filterSchoolId, filterTime)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == com.androidcourse.partner_map.data.repository.Resource.Status.SUCCESS && resource.data != null) {
                        currentRequests = resource.data;
                        viewModel.setRequestList(currentRequests);
                        if (!currentRequests.isEmpty()) {
                            selectRequest(currentRequests.get(0));
                        }
                    }
                });
    }

    private void selectRequest(PartnerRequest request) {
        selectedRequest = request;
        tvCardTitle.setText(CategoryHelper.getLabel(request.getCategory()) + " " + request.getTitle());
        tvCardLocation.setText(String.format("%.2fkm", 0.5));
        tvCardTime.setText(TimeUtil.formatRelative(request.getScheduledTime()) + " 余" + (request.getMaxParticipants() - request.getCurrentParticipants()) + "人");
    }

    public void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        builder.setView(view);
        builder.setTitle("筛选");
        AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_reset).setOnClickListener(v -> {
            filterCategory = null;
            filterSchoolId = null;
            filterRadius = 10000;
            filterTime = null;
            loadRequests();
            dialog.dismiss();
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            loadRequests();
            dialog.dismiss();
        });
        dialog.show();
    }

    private static class MapPlaceholderView extends View {
        private final Paint paint;
        public MapPlaceholderView(android.content.Context context) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setColor(Color.parseColor("#E0E0E0"));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
            paint.setColor(Color.GRAY);
            paint.setTextSize(48);
            String text = "地图区域 (高德地图)";
            float x = getWidth() / 2f - paint.measureText(text) / 2f;
            float y = getHeight() / 2f;
            canvas.drawText(text, x, y, paint);
        }
    }
}
