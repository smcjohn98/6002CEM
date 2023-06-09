package com.smc.crafthk.ui.shop;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityCreateShopBinding;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.viewmodel.CreateShopViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateShopActivity extends AppCompatActivity {

    private ActivityCreateShopBinding binding;

    private CreateShopViewModel viewModel;
    private FirebaseAuth firebaseAuth;

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
        firebaseAuth = FirebaseAuth.getInstance();
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

            if(phone.length() != 8 || !phone.matches("\\d+")){
                Toast.makeText(CreateShopActivity.this, "Phone number must be 8 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            ShopDao shopDao = AppDatabase.getDatabase(getApplicationContext()).shopDao();
            Shop shop = new Shop();
            shop.userId = firebaseAuth.getCurrentUser().getUid();
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
                float defaultZoom = 13.0f;

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
                    }
                });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ResultCode.CHOOSE_IMAGE.getCode()) {
                Uri uri = data.getData();
                imagePath = getRealPathFromURI(uri);
                ImageView imageView = binding.imageShop;
                Glide.with(CreateShopActivity.this)
                        .load(imagePath)
                        .into(imageView);

            } else if (requestCode == ResultCode.TAKE_PHOTO.getCode()) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                File cacheDir = getCacheDir();
                File imageFile = new File(cacheDir, "image.jpg");

                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "IMG_" + timeStamp + ".jpg";
                    String saveImagePath = MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), imageFileName, null);
                    Uri imageUri = Uri.parse(saveImagePath);
                    imagePath = getRealPathFromURI(imageUri);
                    Glide.with(CreateShopActivity.this)
                            .load(imagePath)
                            .into(binding.imageShop);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (requestCode == ResultCode.REQUEST_IMAGE_PERMISSION.getCode()){
                pickImage();
            }
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
        new AlertDialog.Builder(this)
                .setTitle("Upload Image")
                .setItems(new String[]{"Choose Image From Album", "Take Photo"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, ResultCode.CHOOSE_IMAGE.getCode());
                            break;
                        case 1:
                            Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent2, ResultCode.TAKE_PHOTO.getCode());
                            break;
                    }
                })
                .show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}