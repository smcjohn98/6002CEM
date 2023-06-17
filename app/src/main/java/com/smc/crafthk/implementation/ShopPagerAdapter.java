package com.smc.crafthk.implementation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.ui.fragment.EventFragment;
import com.smc.crafthk.ui.fragment.ProductFragment;


public class ShopPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;
    private Context context;
    private int shopId;

    public ShopPagerAdapter(FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public ShopPagerAdapter(int shopId, FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
                ProductFragment productFragment = new ProductFragment();
                productFragment.setArguments(parameters);
                return productFragment;
            case 1:
                EventFragment eventFragment = new EventFragment();
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