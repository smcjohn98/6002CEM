package com.smc.crafthk.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.databinding.ActivityUserProfileBinding;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.viewmodel.UserProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    private UserProfileViewModel liveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        liveData = new ViewModelProvider(this).get(UserProfileViewModel.class);

        TextView textEmail = binding.textEmail;
        TextView textName = binding.textName;
        ImageView avatarImage = binding.avatarImage;
        TextView avatarText = binding.avatarText;

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        textEmail.setText("Email: " + user.getEmail());
        textName.setText("Name: " + user.getDisplayName());

        liveData.getUserProfileImagePath().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String path) {
                if(path == null){
                    String nameInitial = user.getDisplayName().substring(0, 1);
                    avatarText.setText(nameInitial);
                    avatarText.setVisibility(View.VISIBLE);
                    avatarImage.setVisibility(View.GONE);
                }
                else{
                    Glide.with(ProfileActivity.this)
                            .load(path)
                            .circleCrop()
                            .into(avatarImage);

                    avatarImage.setVisibility(View.VISIBLE);
                    avatarText.setVisibility(View.GONE);
                }
            }
        });

        Uri imageUri = user.getPhotoUrl();
        liveData.setUserProfileImagePath(imageUri == null ? null : imageUri.getPath());

        View.OnClickListener avatarOnClickerListener = new View.OnClickListener() {
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
        };

        avatarText.setOnClickListener(avatarOnClickerListener);
        avatarImage.setOnClickListener(avatarOnClickerListener);

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


        binding.buttonClean.setOnClickListener((v)->{
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(null)
                    .build();

            user.updateProfile(profileUpdates);
            liveData.setUserProfileImagePath(null);
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ResultCode.USER_PROFILE_IMAGE.getCode()
                && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            String profileImagePath = getRealPathFromURI(uri);
            /*SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("profile_image_path", profileImagePath);
            editor.apply();*/

            /*ImageView avatarImage = binding.avatarImage;

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
            dao.update(user);*/
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(new Uri.Builder().path(profileImagePath).build())
                    .build();
            user.updateProfile(profileUpdates);
            liveData.setUserProfileImagePath(profileImagePath);
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