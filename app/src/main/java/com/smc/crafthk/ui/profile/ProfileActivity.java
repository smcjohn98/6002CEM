package com.smc.crafthk.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.constraint.Util;
import com.smc.crafthk.databinding.ActivityUserProfileBinding;
import com.smc.crafthk.dto.UserMetadata;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;
import com.smc.crafthk.ui.explore.ExploreActivity;
import com.smc.crafthk.ui.product.CreateProductActivity;
import com.smc.crafthk.ui.shop.CreateShopActivity;
import com.smc.crafthk.ui.shop.ShopActivity;
import com.smc.crafthk.viewmodel.UserProfileViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.crypto.Cipher;

public class ProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    private UserProfileViewModel liveData;
    private FirebaseFirestore firestore;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
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
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firestore = FirebaseFirestore.getInstance();

        user = firebaseAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        textEmail.setText("Email: " + user.getEmail());
        textName.setText("Name: " + user.getDisplayName());

        CollectionReference messagesRef = firestore.collection("user");
        Query query = messagesRef.whereEqualTo("userId", user.getUid());
        query.get().addOnSuccessListener((querySnapshot)->{
            if(querySnapshot.size() == 0){
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task->{
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    UserMetadata userMetadata = new UserMetadata();
                    userMetadata.setUserId(user.getUid());
                    userMetadata.setDeviceToken(token);
                    userMetadata.setEmail(user.getEmail());
                    messagesRef.add(userMetadata);
                });
            }
        });





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
        liveData.setUserProfileImagePath(imageUri == null ? null : imageUri.toString());

        View.OnClickListener avatarOnClickerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    // If the permission is not granted, request it from the user
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            ResultCode.REQUEST_IMAGE_PERMISSION.getCode());
                }
                else {
                    pickImage();
                }
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

        binding.buttonShop.setOnClickListener(v->{
            Intent intent = new Intent(ProfileActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(Constraint.FINGER_PRINT_LOGIN, false)){
            binding.buttonEnableBiometric.setChecked(true);
        }

        binding.buttonEnableBiometric.setOnCheckedChangeListener((buttonView, isChecked)->{
            BiometricManager biometricManager = BiometricManager.from(ProfileActivity.this);
            if(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS){
                Toast.makeText(ProfileActivity.this, "Cannot access biometric manger, please check setting/permission", Toast.LENGTH_SHORT).show();
                buttonView.setChecked(false);
                return;
            }

            if(sharedPreferences.getBoolean(Constraint.FINGER_PRINT_LOGIN, false)){
                editor.putBoolean(Constraint.FINGER_PRINT_LOGIN, false);
                editor.apply();
            }
            else {
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Enable Biometric Login")
                        .setNegativeButtonText("Cancel")
                        .build();

                BiometricPrompt biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        editor.putBoolean(Constraint.FINGER_PRINT_LOGIN, true);
                        editor.apply();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        buttonView.setChecked(!isChecked);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        buttonView.setChecked(!isChecked);
                    }
                });

                biometricPrompt.authenticate(promptInfo);
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ResultCode.CHOOSE_IMAGE.getCode()) {
                try {
                    Uri uri = data.getData();
                    String profileImagePath = getRealPathFromURI(uri);
                    Util.uploadImageToFirebase(profileImagePath, Util.TYPE_PROFILE_IMAGE).thenApply((imageUrl)->{
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(imageUrl)
                                .build();
                        user.updateProfile(profileUpdates);
                        liveData.setUserProfileImagePath(imageUrl.toString());
                        return null;
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
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
                    Util.uploadImageToFirebase(imageFile.getAbsolutePath(), Util.TYPE_PROFILE_IMAGE).thenApply((imageUrl)->{
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(imageUrl)
                                .build();
                        user.updateProfile(profileUpdates);
                        liveData.setUserProfileImagePath(imageUrl.toString());
                        return null;
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(resultCode == ResultCode.REGISTRATION_SUCCEED.getCode()){
                Toast.makeText(ProfileActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ResultCode.REQUEST_IMAGE_PERMISSION.getCode() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

}