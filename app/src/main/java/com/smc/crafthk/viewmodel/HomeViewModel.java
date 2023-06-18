package com.smc.crafthk.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> searchProductName = new MutableLiveData<>();
    private MutableLiveData<Integer> searchProductType = new MutableLiveData<>();
    private MutableLiveData<Integer> searchOffset = new MutableLiveData<>();


    public MutableLiveData<String> getSearchProductName() {
        return searchProductName;
    }

    public void setSearchProductName(String searchProductName) {
        this.searchProductName.setValue(searchProductName);
    }

    public MutableLiveData<Integer> getSearchProductType() {
        return searchProductType;
    }

    public void setSearchProductType(Integer searchProductType) {
        this.searchProductType.setValue(searchProductType);
    }

    public MutableLiveData<Integer> getSearchOffset() {
        return searchOffset;
    }

    public void setSearchOffset(Integer searchOffset) {
        this.searchOffset.setValue(searchOffset);
    }

}