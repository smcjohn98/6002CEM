package com.smc.crafthk.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smc.crafthk.R;
import com.smc.crafthk.dao.UserDao;
import com.smc.crafthk.entity.User;
import com.smc.crafthk.helper.AppDatabase;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText editName = findViewById(R.id.edit_name);
        EditText editEmail = findViewById(R.id.edit_email);
        EditText editPassword = findViewById(R.id.edit_password);
        Button buttonRegister = findViewById(R.id.button_register);

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
                UserDao userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
                if (userDao.getUserByEmail(email) != null) {
                    Toast.makeText(RegistrationActivity.this, "Email address already in use", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new user object
                User user = new User(name, email, password);

                // Add user to database
                userDao.insert(user);

                // Display success message
                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Finish activity and return to login screen
                finish();
            }
        });
    }
}
