package com.smc.crafthk.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationBarView;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.UserDao;
import com.smc.crafthk.databinding.ActivityUserProfileBinding;
import com.smc.crafthk.entity.User;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

import javax.xml.transform.Result;

public class ProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView editEmail = binding.textEmail;
        TextView editPassword = binding.textName;

        SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString("name", "");
        String email = preferences.getString("email", "");

        editEmail.setText("Name: " + name);
        editPassword.setText("Email: " + email);
        String userProfileImagePath = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("profile_image_path", null);

        ImageView avatarImage = binding.avatarImage;
        TextView avatarText = binding.avatarText;
        View avatar = avatarText;
        if (userProfileImagePath != null) {
            Glide.with(this)
                    .load(userProfileImagePath)
                    .circleCrop()
                    .into(avatarImage);

            avatarImage.setVisibility(View.VISIBLE);
            avatarText.setVisibility(View.GONE);
            avatar = avatarImage;
        } else {
            String nameInitial = name.substring(0, 1);
            avatarText.setText(nameInitial);
        }


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If the permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            999);
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), ResultCode.USER_PROFILE_IMAGE.getCode());
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUserLoginStatus();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.buttonClean.setOnClickListener((v)->{
            UserDao dao = AppDatabase.getDatabase(this).userDao();
            User user = dao.getUserByEmail(email);
            user.imagePath = null;
            dao.update(user);
            getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
                    .remove("profile_image_path");

        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    public void clearUserLoginStatus(){
        SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.remove("profile_image_path");
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ResultCode.USER_PROFILE_IMAGE.getCode()
                && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String profileImagePath = getRealPathFromURI(uri);
            SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profile_image_path", profileImagePath);
            editor.apply();
            ImageView avatarImage = binding.avatarImage;

            Glide.with(this)
                    .load(profileImagePath)
                    .circleCrop()
                    .into(avatarImage);

            if(avatarImage.getVisibility() == View.GONE){
                avatarImage.setVisibility(View.VISIBLE);
                binding.avatarText.setVisibility(View.GONE);
            }

            String email = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE).getString("email", "");
            UserDao dao = AppDatabase.getDatabase(this).userDao();
            User user = dao.getUserByEmail(email);
            user.imagePath = profileImagePath;
            dao.update(user);
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
}