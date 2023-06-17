package com.smc.crafthk.ui.event;

import static com.smc.crafthk.constraint.Constraint.SHOP_ID_INTENT_EXTRA;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.EventDao;
import com.smc.crafthk.dao.ProductDao;
import com.smc.crafthk.databinding.ActivityCreateEventBinding;
import com.smc.crafthk.databinding.ActivityCreateProductBinding;
import com.smc.crafthk.entity.Event;
import com.smc.crafthk.entity.Product;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.ui.shop.ShopPagerActivity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private ActivityCreateEventBinding binding;
    private FirebaseAuth mAuth;
    String imagePath;

    LocalDateTime eventDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        EditText editEventName = binding.editEventName;
        EditText editPrice = binding.editPrice;
        EditText editDescription = binding.editDescription;
        //EditText editTime = binding.editEventTime;
        Button buttonImage = binding.buttonImage;
        Button buttonCreate = binding.buttonCreate;

        Button dateTimePickerButton = binding.buttonPickDatetime;


        dateTimePickerButton.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, monthOfYear, dayOfMonth) -> {
                calendar.set(year, monthOfYear, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    eventDateTime = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
                    binding.textEventTimeValue.setText(eventDateTime.toString());
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show();

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        int shopId = getIntent().getIntExtra(SHOP_ID_INTENT_EXTRA, -1);

        buttonCreate.setOnClickListener((v) -> {
            String eventName = editEventName.getText().toString();
            String description = editDescription.getText().toString();
            String price = editPrice.getText().toString();


            if (eventName.isEmpty() || description.isEmpty() || price.isEmpty() || eventDateTime == null)  {
                Toast.makeText(CreateEventActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            EventDao eventDao = AppDatabase.getDatabase(getApplicationContext()).eventDao();
            Event event = new Event();
            event.shopId = shopId;
            event.price = new BigDecimal(price);
            event.name = eventName;
            event.description = description;
            event.dateTime = eventDateTime;
            event.imagePath = imagePath;
            eventDao.insert(event);
            Intent intent = new Intent(CreateEventActivity.this, ShopPagerActivity.class);
            startActivity(intent);
            finish();
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If the permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(CreateEventActivity.this,
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

            ImageView imageView = binding.imageEvent;
            Glide.with(CreateEventActivity.this)
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