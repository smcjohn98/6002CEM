package com.smc.crafthk.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityShopBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.ShopAdapter;
import com.smc.crafthk.ui.shop.CreateShopActivity;
import com.smc.crafthk.ui.shop.ShopPagerActivity;

import java.util.List;

public class ShopViewActivity extends AppCompatActivity {

    private ActivityShopBinding binding;

    private FirebaseAuth mAuth;

    private Double longitude, latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        binding.buttonCreate.setOnClickListener(v->{
            Intent intent = new Intent(ShopViewActivity.this, CreateShopActivity.class);
            startActivity(intent);
            finish();
        });


        ShopDao shopDao = AppDatabase.getDatabase(getApplicationContext()).shopDao();
        List<Shop> shopList = shopDao.getShopByUserId(mAuth.getCurrentUser().getUid());
        RecyclerView recyclerView = binding.listShop;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShopAdapter adapter = new ShopAdapter(shopList, position->{
            Shop shop = shopList.get(position);
            Intent intent = new Intent(ShopViewActivity.this, ShopPagerActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, shop.id);
            startActivity(intent);
            //Toast.makeText(ShopActivity.this, shop.name + " clicked", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }
}