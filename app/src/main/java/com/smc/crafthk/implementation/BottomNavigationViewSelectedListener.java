package com.smc.crafthk.implementation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.ui.profile.LoginActivity;
import com.smc.crafthk.ui.profile.ProfileActivity;

public class BottomNavigationViewSelectedListener implements NavigationBarView.OnItemSelectedListener{
    private Context context;

    public BottomNavigationViewSelectedListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.home){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            return true;
        }
        else if(itemId == R.id.profile){
            SharedPreferences preferences = context.getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            if(preferences.getString("name", "").length() > 0){
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
                return true;
            }
            else{
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }
}
