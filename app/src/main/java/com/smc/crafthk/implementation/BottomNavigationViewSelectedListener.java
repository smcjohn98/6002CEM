package com.smc.crafthk.implementation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.ui.explore.ExploreActivity;
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
        Class<? extends Activity> activityClass = ((Activity) context).getClass();
        if(itemId == R.id.home){
            if(activityClass == MainActivity.class){
                return false;
            }
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            return true;
        }
        else if(itemId == R.id.profile){
            if(activityClass == LoginActivity.class || activityClass == ProfileActivity.class ){
                return false;
            }

            //SharedPreferences preferences = context.getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            //preferences.getString("name", "").length() > 0
            if(firebaseAuth.getCurrentUser() != null){
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
        else if(itemId == R.id.explore){
            Intent intent = new Intent(context, ExploreActivity.class);
            context.startActivity(intent);
            return false;
        }
        return false;
    }
}
