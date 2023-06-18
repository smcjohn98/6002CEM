package com.smc.crafthk.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.crafthk.dao.EventDao;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.FragmentShopViewEventBinding;
import com.smc.crafthk.databinding.FragmentShopViewProductBinding;
import com.smc.crafthk.entity.EventWithShopInfo;
import com.smc.crafthk.entity.ProductWithShopInfo;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.EventAdapter;
import com.smc.crafthk.implementation.EventWithShopInfoAdapter;
import com.smc.crafthk.implementation.ProductWithShopInfoAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HomeEventFragment extends Fragment {

    FragmentShopViewEventBinding binding;
    EventWithShopInfoAdapter adapter;

    private int pageOfSize = 6;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShopViewEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecyclerView eventView = binding.listEvent;
        eventView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventWithShopInfoAdapter(new ArrayList<>(), position->{});
        eventView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventDao eventDao = AppDatabase.getDatabase(getContext()).eventDao();
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        List<EventWithShopInfo> eventList = eventDao.getUpcomingEventsWithShopInfo(pageOfSize, 0, localDateTime);
        adapter.setData(eventList);
        adapter.notifyDataSetChanged();
    }
}