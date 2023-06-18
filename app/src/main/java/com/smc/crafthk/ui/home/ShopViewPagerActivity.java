package com.smc.crafthk.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityShopPagerBinding;
import com.smc.crafthk.databinding.ActivityShopViewPagerBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.ShopPagerAdapter;
import com.smc.crafthk.implementation.ShopViewPagerAdapter;

public class ShopViewPagerActivity extends AppCompatActivity {

    private ActivityShopViewPagerBinding binding;

    private Integer shopId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopViewPagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        shopId = getIntent().getIntExtra(Constraint.SHOP_ID_INTENT_EXTRA, -1);

        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager = binding.viewPager;

        ShopDao shopDao = AppDatabase.getDatabase(this).shopDao();
        Shop shop = shopDao.getShopById(shopId);
        binding.textShopName.setText(shop.name);
        binding.textShopAddress.setText(Util.getAddress(this, new LatLng(shop.latitude, shop.longitude)));
        binding.textShopDesc.setText(shop.shopDescription);
        Glide.with(this).load(shop.shopImagePath).circleCrop().into(binding.iamgeShopImage);

        ShopViewPagerAdapter adapter = new ShopViewPagerAdapter(shopId, getSupportFragmentManager(), getLifecycle());
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