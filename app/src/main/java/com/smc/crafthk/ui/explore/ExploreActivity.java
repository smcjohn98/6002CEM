package com.smc.crafthk.ui.explore;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityCreateProductBinding;
import com.smc.crafthk.databinding.ActivityExploreBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.ui.home.ShopViewPagerActivity;
import com.smc.crafthk.ui.profile.ProfileActivity;
import com.smc.crafthk.ui.shop.CreateShopActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExploreActivity extends AppCompatActivity {
    ActivityExploreBinding binding;
    Map<Marker, Shop> markerToShop;

    Double currentLatitude, currentLongitude;
    GoogleMap googleMap;

    Circle circle;

    Shop selectedShop;

    FirebaseFirestore firestore;

    boolean getLocationFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyAM1SEv2w5kAAUqVsr--b0p0IfwdCT7Ssg");
        }

        firestore = FirebaseFirestore.getInstance();
        markerToShop = new HashMap<>();

        ShopDao shopDao = AppDatabase.getDatabase(this).shopDao();
        List<Shop> shopList = shopDao.getAllShops();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        View mapView = mapFragment.getView();

        if (ActivityCompat.checkSelfPermission(ExploreActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ExploreActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            setGPSListener();
        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    ResultCode.PERMISSION_LOCATION.getCode());
        }



        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                ExploreActivity.this.googleMap = googleMap;

                LatLng location = new LatLng(22.319899, 114.174467);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                for(Shop shop:shopList){
                    LatLng latLng = new LatLng(shop.latitude, shop.longitude);
                    MarkerOptions markerOptions = new MarkerOptions().title(shop.name)
                            .snippet(Util.getAddress(ExploreActivity.this, latLng))
                            .position(latLng);

                    Marker marker = googleMap.addMarker(markerOptions);
                    markerToShop.put(marker, shop);
                }

                googleMap.setOnMarkerClickListener(marker->{
                    Shop shop = markerToShop.get(marker);
                    if(shop != null){
                        selectedShop = shop;
                        binding.buttonView.setVisibility(View.VISIBLE);
                        binding.textShopId.setText("Shop : "+shop.name);
                        if(currentLatitude != null){
                            float[] results = new float[1];
                            Location.distanceBetween(shop.latitude, shop.longitude, currentLatitude, currentLongitude, results);
                            binding.textDistance.setText("Distance(m) : "+String.valueOf(results[0]));
                        }
                    }
                    return false;
                });
            }
        });

        binding.buttonView.setOnClickListener(v->{
            Intent intent = new Intent(ExploreActivity.this, ShopViewPagerActivity.class);
            intent.putExtra(Constraint.SHOP_ID_INTENT_EXTRA, selectedShop.id);
            startActivity(intent);
        });

        binding.buttonSwitch.setOnCheckedChangeListener((buttonView, isChecked)->{
            if(currentLongitude == null){
                Toast.makeText(ExploreActivity.this, "Cannot find your location, please check GPS setting/permission", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(!isChecked);
                return;
            }

            if(isChecked){
                if(currentLatitude != null){
                    LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                    CircleOptions circleOptions = new CircleOptions()
                            .center(latLng)
                            .radius(1000.0f)
                            .strokeWidth(2)
                            .strokeColor(Color.RED)
                            .fillColor(Color.argb(50, 255, 0, 0));

                    circle = googleMap.addCircle(circleOptions);

                    for(Marker marker:markerToShop.keySet()){
                        float[] results = new float[1];
                        Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, currentLatitude, currentLongitude, results);
                        if(results[0] > 1000.0f)
                            marker.setVisible(false);
                    }
                }
            }
            else{
                circle.remove();
                circle = null;

                for(Marker marker:markerToShop.keySet()){
                    marker.setVisible(true);
                }
            }
        });

        binding.buttonBack.setOnClickListener(v->{
            finish();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ResultCode.PERMISSION_LOCATION.getCode() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setGPSListener();
        }
    }

    @SuppressLint("MissingPermission")
    public void setGPSListener(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (location -> {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if(!getLocationFinish && googleMap != null){
                LatLng latLng = new LatLng(currentLatitude, currentLongitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                getLocationFinish = true;
            }
        }));
    }
}
