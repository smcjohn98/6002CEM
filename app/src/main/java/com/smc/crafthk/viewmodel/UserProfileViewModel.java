package com.smc.crafthk.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserProfileViewModel extends ViewModel {

    private MutableLiveData<String> displayName = new MutableLiveData<>();
    private MutableLiveData<String> userProfileImagePath = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public MutableLiveData<String> getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName.setValue(displayName);
    }

    public MutableLiveData<String> getUserProfileImagePath() {
        return userProfileImagePath;
    }

    public void setUserProfileImagePath(String userProfileImagePath) {
        this.userProfileImagePath.setValue(userProfileImagePath);
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }
}