package com.smc.crafthk.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.databinding.ActivityRegistrationBinding;
import com.smc.crafthk.dto.UserMetadata;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    private FirebaseAuth firebaseAuth;

    private Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        EditText editName = binding.editName;
        EditText editEmail = binding.editEmail;
        EditText editPassword = binding.editPassword;
        EditText editPasswordConfirmation = binding.editPasswordConfirmation;
        Button buttonRegister = binding.buttonRegister;

        // Add click listener to register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from text fields
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                String passwordConfirmation = editPasswordConfirmation.getText().toString();

                // Validate input
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
                    Toast.makeText(RegistrationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pattern.matcher(email).matches()){
                    Toast.makeText(RegistrationActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(passwordConfirmation)){
                    Toast.makeText(RegistrationActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();


                                    user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    setResult(ResultCode.REGISTRATION_SUCCEED.getCode());
                                                    Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                                } else {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseAuthWeakPasswordException) {
                                        Toast.makeText(RegistrationActivity.this, "Weak Password",
                                                Toast.LENGTH_SHORT).show();
                                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(RegistrationActivity.this, "Invalid email format",
                                                Toast.LENGTH_SHORT).show();
                                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegistrationActivity.this, "Email is existed",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        });
            }
        });

        binding.bottomNavigationView.setSelectedItemId(R.id.profile);

        binding.bottomNavigationView.setOnItemSelectedListener(new BottomNavigationViewSelectedListener(this));
    }
}