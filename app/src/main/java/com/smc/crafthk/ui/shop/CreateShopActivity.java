package com.smc.crafthk.ui.shop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityCreateShopBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.viewmodel.CreateShopViewModel;

public class CreateShopActivity extends AppCompatActivity {

    private ActivityCreateShopBinding binding;

    private CreateShopViewModel viewModel;
    private FirebaseAuth mAuth;

    private SupportMapFragment mapFragment;

    TextView textLocationValue;
    TextView textAddress;

    String imagePath;
    private Double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(CreateShopViewModel.class);

        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyAM1SEv2w5kAAUqVsr--b0p0IfwdCT7Ssg");
        }


        EditText editShopName = binding.editShopName;
        EditText editPhone = binding.editPhone;
        EditText editDescription = binding.editDescription;
        Button buttonImage = binding.buttonImage;
        Button buttonCreate = binding.buttonCreate;
        textLocationValue = binding.textLocationValue;
        textAddress = binding.textAddress;

        buttonCreate.setOnClickListener((v) -> {
            String shopName = editShopName.getText().toString();
            String phone = editPhone.getText().toString();
            String description = editDescription.getText().toString();

            if (shopName.isEmpty() || phone.isEmpty() || description.isEmpty() || longitude == null || imagePath == null) {
                Toast.makeText(CreateShopActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ShopDao shopDao = AppDatabase.getDatabase(getApplicationContext()).shopDao();
            Shop shop = new Shop();
            shop.userId = mAuth.getCurrentUser().getUid();
            shop.name = shopName;
            shop.shopPhoneNumber = phone;
            shop.shopDescription = description;
            shop.longitude = longitude;
            shop.latitude = latitude;
            shop.shopImagePath = imagePath;
            shopDao.insert(shop);
            Intent intent = new Intent(CreateShopActivity.this, ShopActivity.class);
            startActivity(intent);
            finish();
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If the permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(CreateShopActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            ResultCode.REQUEST_IMAGE_PERMISSION.getCode());
                } else {
                    pickImage();
                }
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        View mapView = mapFragment.getView();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                LatLng defaultLocation = new LatLng(22.319899, 114.174467);
                float defaultZoom = 18.0f;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(defaultLocation)
                        .zoom(defaultZoom)
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        googleMap.clear();
                        googleMap.addMarker(markerOptions);

                        longitude = latLng.longitude;
                        latitude = latLng.latitude;
                        textLocationValue.setText(String.format("(%f, %f)", latitude, longitude));
                        binding.textAddress.setText(Util.getAddress(CreateShopActivity.this, new LatLng(latitude, longitude)));
                        /*Geocoder geocoder = new Geocoder(CreateShopActivity.this, Locale.getDefault());
                        try {
                            // Get the address from the Geocoder
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                            if (addresses != null && !addresses.isEmpty()) {
                                // Get the first address from the list
                                Address address = addresses.get(0);

                                // Get the address lines and join them into a string
                                List<String> addressLines = new ArrayList<>();
                                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                    addressLines.add(address.getAddressLine(i));
                                }
                                String addressString = TextUtils.join(", ", addressLines);
                                binding.textAddress.setText(addressString);
                                // Use the address string
                                Log.d(TAG, "Address: " + addressString);
                            } else {
                                Log.d(TAG, "Address not found");
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Geocoder exception: " + e.getMessage());
                        }*/
                    }
                });
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ResultCode.SHOP_IMAGE.getCode()) {

            Uri uri = data.getData();
            imagePath = getRealPathFromURI(uri);

            ImageView imageView = binding.imageShop;
            Glide.with(CreateShopActivity.this)
                    .load(imagePath)
                    .into(imageView);
        }
        else if (requestCode == ResultCode.REQUEST_IMAGE_PERMISSION.getCode()){
            pickImage();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ResultCode.SHOP_IMAGE.getCode());
    }
}