package com.smc.crafthk.ui.product;

import static android.content.ContentValues.TAG;

import static com.smc.crafthk.constraint.Constraint.SHOP_ID_INTENT_EXTRA;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.smc.crafthk.constraint.CraftType;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.dao.ShopDao;
import com.smc.crafthk.databinding.ActivityCreateProductBinding;
import com.smc.crafthk.databinding.ActivityCreateShopBinding;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.entity.Shop;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.ui.shop.ShopActivity;
import com.smc.crafthk.ui.shop.ShopPagerActivity;
import com.smc.crafthk.viewmodel.CreateShopViewModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateProductActivity extends AppCompatActivity {

    private ActivityCreateProductBinding binding;
    private FirebaseAuth mAuth;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        EditText editProductName = binding.editProductName;
        EditText editPrice = binding.editPrice;
        EditText editDescription = binding.editDescription;
        Button buttonImage = binding.buttonImage;
        Button buttonCreate = binding.buttonCreate;
        Spinner spinner = binding.spinnerType;

        ArrayAdapter<CraftType> adapter = new ArrayAdapter<CraftType>(
                this, android.R.layout.simple_spinner_item, CraftType.values()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setText(CraftType.values()[position].getName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setText(CraftType.values()[position].getName());
                return view;
            }
        };

        spinner.setAdapter(adapter);

        int shopId = getIntent().getIntExtra(SHOP_ID_INTENT_EXTRA, -1);

        buttonCreate.setOnClickListener((v) -> {
            String productName = editProductName.getText().toString();
            String description = editDescription.getText().toString();
            String price = editPrice.getText().toString();
            CraftType type = (CraftType)spinner.getSelectedItem();

            if (productName.isEmpty() || description.isEmpty() || price.isEmpty() || type == null) {
                Toast.makeText(CreateProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductDao productDao = AppDatabase.getDatabase(getApplicationContext()).productDao();
            Product product = new Product();
            product.shopId = shopId;
            product.price = new BigDecimal(price);
            product.name = productName;
            product.type = type.getId();
            product.description = description;
            product.imagePath = imagePath;
            productDao.insert(product);
            //Intent intent = new Intent(CreateProductActivity.this, ShopPagerActivity.class);
            //startActivity(intent);
            finish();
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If the permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(CreateProductActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            ResultCode.REQUEST_IMAGE_PERMISSION.getCode());
                }
                else {
                    pickImage();
                }
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ResultCode.PRODUCT_IMAGE.getCode()) {

            Uri uri = data.getData();
            imagePath = getRealPathFromURI(uri);

            ImageView imageView = binding.imageProduct;
            Glide.with(CreateProductActivity.this)
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ResultCode.PRODUCT_IMAGE.getCode());
    }
}