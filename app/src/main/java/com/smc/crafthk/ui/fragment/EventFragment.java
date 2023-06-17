package com.smc.crafthk.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.databinding.FragmentEventBinding;
import com.smc.crafthk.ui.event.CreateEventActivity;
import com.smc.crafthk.ui.product.CreateProductActivity;

public class EventFragment extends Fragment {
    FragmentEventBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        int shopId = bundle.getInt(Constraint.SHOP_ID_INTENT_EXTRA);

        binding.buttonCreate.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, shopId);
            startActivity(intent);
        });

        return view;
    }
}