package com.smc.crafthk.ui.event;

import static com.smc.crafthk.constraint.Constraint.SHOP_ID_INTENT_EXTRA;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.EventDao;
import com.smc.crafthk.databinding.ActivityCreateEventBinding;
import com.smc.crafthk.entity.Event;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.ui.product.CreateProductActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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

                    LocalDateTime selectedDateTime = LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
                    if(selectedDateTime.isBefore(LocalDateTime.now().plusMinutes(30))){
                        new AlertDialog.Builder(this)
                                .setTitle("Alert")
                                .setMessage("Selected date/time must more than 30 minutes after now")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                    else {
                        eventDateTime = selectedDateTime;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        binding.textEventTimeValue.setText(formatter.format(eventDateTime));
                    }
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
            event.eventPrice = new BigDecimal(price);
            event.eventName = eventName;
            event.eventDescription = description;
            event.eventDateTime = eventDateTime;
            event.eventImagePath = imagePath;
            eventDao.insert(event);
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
                ImageView imageView = binding.imageEvent;
                Glide.with(CreateEventActivity.this)
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
                    Glide.with(CreateEventActivity.this)
                            .load(imagePath)
                            .into(binding.imageEvent);
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