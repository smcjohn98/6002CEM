package com.smc.crafthk.constraint;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smc.crafthk.ui.shop.CreateShopActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class Util {
    public static String TYPE_PROFILE_IMAGE = "profile_image";
    public static String TYPE_PRODUCT_IMAGE = "product_image";
    public static String TYPE_EVENT_IMAGE = "event_image";
    private static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private static StorageReference storageReference = firebaseStorage.getReference();

    public static String getAddress(Context context, LatLng latLng){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            // Get the address from the Geocoder
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                // Get the first address from the list
                Address address = addresses.get(0);

                // Get the address lines and join them into a string
                List<String> addressLines = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressLines.add(address.getAddressLine(i));
                }
                String addressString = TextUtils.join(", ", addressLines);
                return addressString;
            } else {
                Log.d(TAG, "Address not found");
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder exception: " + e.getMessage());
        }
        return "";
    }

    public static CompletableFuture<Uri> uploadImageToFirebase(String filePath, String type){
        Long timestamp = System.currentTimeMillis();
        StorageReference storageRef = storageReference.child("crafthk/" + type + "/" + timestamp + ".jpg");
        CompletableFuture<Uri> future = new CompletableFuture<>();

        try {
            byte[] data = Files.readAllBytes(new File(filePath).toPath());
            UploadTask uploadTask = storageRef.putBytes(data);

            Log.d("uploadImageToFirebase", "UploadTask started");

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d("uploadImageToFirebase", "UploadTask succeeded");

                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("uploadImageToFirebase", "getDownloadUrl succeeded");
                    future.complete(uri);
                }).addOnFailureListener(e -> {
                    Log.e("uploadImageToFirebase", "getDownloadUrl failed", e);
                    future.completeExceptionally(e);
                });
            }).addOnFailureListener(e -> {
                Log.e("uploadImageToFirebase", "UploadTask failed", e);
                future.completeExceptionally(e);
            });
        } catch (Exception e) {
            Log.e("uploadImageToFirebase", "Exception occurred", e);
            future.completeExceptionally(e);
        }

        return future;
    }
}
