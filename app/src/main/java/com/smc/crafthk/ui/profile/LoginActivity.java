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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.UserDao;
import com.smc.crafthk.databinding.ActivityLoginBinding;
import com.smc.crafthk.entity.User;
import com.smc.crafthk.helper.AppDatabase;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == ResultCode.REGISTRATION_SUCCEED.getCode()){
            Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView textRegister = binding.textRegister;
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        EditText editEmail = binding.editEmail;
        EditText editPassword = binding.editPassword;

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from text fields
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                // Validate input
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pattern.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this, "Not correct email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                password = DigestUtils.md5Hex(password).toUpperCase();

                UserDao userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
                User user = userDao.getUserByEmail(email);
                if (user == null) {
                    Toast.makeText(LoginActivity.this, "User is not existed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(user.password)) {
                    Toast.makeText(LoginActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                saveUserLoginStatus(user);
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);
        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }

    public void saveUserLoginStatus(User user){
        SharedPreferences preferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", user.name);
        editor.putString("email", user.email);
        editor.apply();
    }

}