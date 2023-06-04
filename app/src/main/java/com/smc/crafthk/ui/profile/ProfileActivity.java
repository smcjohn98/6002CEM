package com.smc.crafthk.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.databinding.ActivityUserProfileBinding;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

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

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUserLoginStatus();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    public void clearUserLoginStatus(){
        SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.apply();
    }

}