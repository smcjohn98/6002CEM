package com.smc.crafthk.implementation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.ui.fragment.HomeEventFragment;
import com.smc.crafthk.ui.fragment.HomeProductFragment;
import com.smc.crafthk.ui.fragment.ShopViewEventFragment;
import com.smc.crafthk.ui.fragment.ShopViewProductFragment;


public class HomePagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;
    private Context context;

    public HomePagerAdapter(FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                HomeProductFragment productFragment = new HomeProductFragment();
                return productFragment;
            case 1:
                HomeEventFragment eventFragment = new HomeEventFragment();
                return eventFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}