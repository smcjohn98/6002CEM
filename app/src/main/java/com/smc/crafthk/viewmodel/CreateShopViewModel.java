package com.smc.crafthk.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateShopViewModel extends ViewModel {

    private MutableLiveData<String> imagePath = new MutableLiveData<>();


    public MutableLiveData<String> getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath.setValue(imagePath);
    }

}