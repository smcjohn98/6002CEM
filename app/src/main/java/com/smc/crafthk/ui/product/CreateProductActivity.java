package com.smc.crafthk.ui.product;

import static com.smc.crafthk.constraint.Constraint.SHOP_ID_INTENT_EXTRA;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.CraftType;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.ActivityCreateProductBinding;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

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

            if (productName.isEmpty() || description.isEmpty() || price.isEmpty() || type == null || type == CraftType.NONE) {
                Toast.makeText(CreateProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ProductDao productDao = AppDatabase.getDatabase(getApplicationContext()).productDao();
            Product product = new Product();
            product.shopId = shopId;
            product.productPrice = new BigDecimal(price);
            product.productName = productName;
            product.productType = type.getId();
            product.productDescription = description;
            product.productImagePath = imagePath;
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
                ImageView imageView = binding.imageProduct;
                Glide.with(CreateProductActivity.this)
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
                    Glide.with(CreateProductActivity.this)
                            .load(imagePath)
                            .into(binding.imageProduct);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
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