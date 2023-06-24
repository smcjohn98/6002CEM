package com.smc.crafthk;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smc.crafthk.R;
import com.smc.crafthk.constraint.ResultCode;
import com.smc.crafthk.ui.profile.ProfileActivity;
import com.smc.crafthk.ui.profile.RegistrationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    @Rule
    public ActivityScenarioRule<RegistrationActivity> activityScenarioRule = new ActivityScenarioRule<>(RegistrationActivity.class);

    @Test
    public void testRegistration() {
        // Mock FirebaseAuth
        FirebaseAuth firebaseAuth = Mockito.mock(FirebaseAuth.class);
        FirebaseAuth.AuthStateListener authStateListener = Mockito.mock(FirebaseAuth.AuthStateListener.class);
        FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);
        Mockito.when(firebaseAuth.getCurrentUser()).thenReturn(firebaseUser);

        // Launch the activity
        ActivityScenario<RegistrationActivity> scenario = activityScenarioRule.getScenario();

        // Wait for the activity to be created and visible
        Espresso.onView(ViewMatchers.withId(R.id.edit_name)).perform(ViewActions.typeText("Test User"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_email)).perform(ViewActions.typeText("testuser@example.com"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_password)).perform(ViewActions.typeText("password"));
        Espresso.onView(ViewMatchers.withId(R.id.edit_password_confirmation)).perform(ViewActions.typeText("password"));
        Espresso.onView(ViewMatchers.withId(R.id.button_register)).perform(ViewActions.click());


        verify(firebaseAuth).createUserWithEmailAndPassword(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        // Verify that the registration was successful
        //verify(firebaseAuth, Mockito.times(1)).createUserWithEmailAndPassword(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        /*verify(firebaseUser, Mockito.times(1)).updateProfile(ArgumentMatchers.any());
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("email", "testuser@example.com");
        intent.putExtra("name", "Test User");
        verify(activityScenarioRule.getScenario().getResult().getResultData(), Mockito.times(1)).putExtra("email", "testuser@example.com");
        verify(activityScenarioRule.getScenario().getResult().getResultData(), Mockito.times(1)).putExtra("name", "Test User");*/
        //verify(activityScenarioRule.getScenario().getResult(), Mockito.times(1)).setResult(ResultCode.REGISTRATION_SUCCEED.getCode(), intent);
        //verify(activityScenarioRule.getScenario().getResult(), Mockito.times(1)).finish();
    }
}