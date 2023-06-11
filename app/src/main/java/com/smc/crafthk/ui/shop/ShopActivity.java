package com.smc.crafthk.ui.shop;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityCreateShopBinding;
import com.smc.crafthk.databinding.ActivityShopBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.implementation.ShopAdapter;
import com.smc.crafthk.viewmodel.CreateShopViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {

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
            Intent intent = new Intent(ShopActivity.this, CreateShopActivity.class);
            startActivity(intent);
            finish();
        });


        ShopDao shopDao = AppDatabase.getDatabase(getApplicationContext()).shopDao();
        List<Shop> shopList = shopDao.getShopByUserId(mAuth.getCurrentUser().getUid());
        RecyclerView recyclerView = binding.listShop;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShopAdapter adapter = new ShopAdapter(shopList);
        recyclerView.setAdapter(adapter);

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }
}