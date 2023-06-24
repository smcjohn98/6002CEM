package com.smc.crafthk.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.Constraint;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.databinding.ActivityLoginBinding;
import com.smc.crafthk.implementation.BottomNavigationViewSelectedListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth firebaseAuth;
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
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }

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

        BiometricManager biometricManager = BiometricManager.from(this);

        if(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS){
            SharedPreferences sharedPreferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
            if(sharedPreferences.getBoolean(Constraint.FINGER_PRINT_LOGIN, false)){
                binding.buttonFingerPrintLogin.setVisibility(View.VISIBLE);
                binding.buttonFingerPrintLogin.setOnClickListener((v)->{

                    BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric Login")
                            .setNegativeButtonText("Cancel")
                            .build();

                    String email = sharedPreferences.getString(Constraint.FINGER_PRINT_EMAIL, "");
                    String password = sharedPreferences.getString(Constraint.FINGER_PRINT_PASSWORD, "");
                    BiometricPrompt biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Exception e = task.getException();
                                                if (e instanceof FirebaseAuthInvalidUserException) {
                                                    Toast.makeText(LoginActivity.this, "User is not existed", Toast.LENGTH_SHORT).show();
                                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                    Toast.makeText(LoginActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            // Authentication error, do something
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            // Authentication failed, do something
                        }
                    });

                    biometricPrompt.authenticate(promptInfo);
                });

            }
        }

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

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(Constraint.SHARE_PREFERENCE_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    // The bad practice to store the user credential for enabling biometric login...
                                    editor.putString(Constraint.FINGER_PRINT_EMAIL, email);
                                    editor.putString(Constraint.FINGER_PRINT_PASSWORD, password);
                                    editor.putBoolean(Constraint.FINGER_PRINT_LOGIN, false);
                                    editor.apply();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(LoginActivity.this, "User is not existed", Toast.LENGTH_SHORT).show();
                                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(LoginActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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