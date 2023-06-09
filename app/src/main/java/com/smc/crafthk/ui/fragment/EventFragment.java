package com.smc.crafthk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.dao.EventDao;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.FragmentEventBinding;
import com.smc.crafthk.entity.Event;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.EventAdapter;
import com.smc.crafthk.ui.event.CreateEventActivity;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment {
    FragmentEventBinding binding;
    EventAdapter adapter;
    int shopId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        shopId = bundle.getInt(Constraint.SHOP_ID_INTENT_EXTRA);


        RecyclerView eventView = binding.listEvent;
        eventView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(new ArrayList<>(), position->{});
        eventView.setAdapter(adapter);
        
        binding.buttonCreate.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, shopId);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventDao eventDao = AppDatabase.getDatabase(getContext()).eventDao();
        List<Event> eventList = eventDao.getEventByShopId(shopId);
        adapter.setData(eventList);
        adapter.notifyDataSetChanged();
    }
}