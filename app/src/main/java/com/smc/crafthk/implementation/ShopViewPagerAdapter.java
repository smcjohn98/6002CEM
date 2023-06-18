package com.smc.crafthk.implementation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.ui.fragment.EventFragment;
import com.smc.crafthk.ui.fragment.ProductFragment;
import com.smc.crafthk.ui.fragment.ShopViewEventFragment;
import com.smc.crafthk.ui.fragment.ShopViewProductFragment;


public class ShopViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;
    private Context context;
    private int shopId;

    public ShopViewPagerAdapter(FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public ShopViewPagerAdapter(int shopId, FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.shopId = shopId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle parameters = new Bundle();
        parameters.putInt(Constraint.SHOP_ID_INTENT_EXTRA, shopId);

        switch (position) {
            case 0:
                ShopViewProductFragment productFragment = new ShopViewProductFragment();
                productFragment.setArguments(parameters);
                return productFragment;
            case 1:
                ShopViewEventFragment eventFragment = new ShopViewEventFragment();
                eventFragment.setArguments(parameters);
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