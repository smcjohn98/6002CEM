package com.smc.crafthk.ui.home;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityMainBinding;
import com.smc.crafthk.databinding.ActivityShopViewPagerBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.HomePagerAdapter;
import com.smc.crafthk.implementation.ShopViewPagerAdapter;

public class HomePagerActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Integer shopId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager = binding.viewPager;

        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Product");
                    break;
                case 1:
                    tab.setText("Event");
                    break;
            }
        }).attach();

        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

}