package com.androidcourse.partner_map.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcourse.partner_map.R;
import com.androidcourse.partner_map.model.PartnerRequest;
import com.androidcourse.partner_map.view.activity.RequestDetailActivity;
import com.androidcourse.partner_map.view.adapter.RequestListAdapter;
import com.androidcourse.partner_map.viewmodel.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class RequestListFragment extends Fragment {
    private MapViewModel viewModel;
    private RequestListAdapter adapter;
    private final List<PartnerRequest> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RequestListAdapter(data, request -> {
            Intent intent = new Intent(requireContext(), RequestDetailActivity.class);
            intent.putExtra(RequestDetailActivity.EXTRA_REQUEST_ID, request.getRequestId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MapViewModel.class);
        viewModel.getRequestList().observe(getViewLifecycleOwner(), list -> {
            data.clear();
            if (list != null) data.addAll(list);
            adapter.notifyDataSetChanged();
        });
        return view;
    }
}
