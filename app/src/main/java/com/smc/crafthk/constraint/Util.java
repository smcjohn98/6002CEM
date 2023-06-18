package com.smc.crafthk.constraint;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.smc.crafthk.ui.shop.CreateShopActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Util {
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
}
