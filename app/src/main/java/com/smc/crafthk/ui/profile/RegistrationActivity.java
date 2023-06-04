package com.smc.crafthk.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.smc.crafthk.MainActivity;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.dao.UserDao;
import com.smc.crafthk.databinding.ActivityRegistrationBinding;
import com.smc.crafthk.entity.User;
import com.smc.crafthk.helper.AppDatabase;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    private Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText editName = binding.editName;
        EditText editEmail = binding.editEmail;
        EditText editPassword = binding.editPassword;
        Button buttonRegister = binding.buttonRegister;

        // Add click listener to register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from text fields
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                // Validate input
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                password = DigestUtils.md5Hex(password).toUpperCase();
                if (!pattern.matcher(email).matches()){
                    Toast.makeText(RegistrationActivity.this, "Not correct email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserDao userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
                if (userDao.getUserByEmail(email) != null) {
                    Toast.makeText(RegistrationActivity.this, "Email address already in use", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new user object
                User user = new User(name, email, password);

                // Add user to database
                userDao.insert(user);

                setResult(ResultCode.REGISTRATION_SUCCEED.getCode());
                // Display success message
                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Finish activity and return to login screen
                finish();
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
}