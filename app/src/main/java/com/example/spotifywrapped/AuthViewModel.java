package com.example.spotifywrapped;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    // keeps track of which spotify request is currently being retrieved
    private MutableLiveData<Integer> retrieved;

    public AuthViewModel() {
        retrieved = new MutableLiveData<>();
        retrieved.setValue(0);
    }
    public LiveData<Integer> getRetrieved() {
        Integer value = retrieved.getValue();
        if (value == null) {
            value = 0;
        }
        return retrieved;
    }

    public void setRetrieved(Integer retrievedValue) {
        this.retrieved.postValue(++retrievedValue);
    }
}