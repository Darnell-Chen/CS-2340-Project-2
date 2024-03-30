package com.example.spotifywrapped;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    // keeps track of which spotify request is currently being retrieved
    private Integer requestRetrieved;
    private MutableLiveData<Integer> rangeRetrieved;

    // this will define how many term ranges we have (short, medium, long)
    private final int max_range = 3;

    public AuthViewModel() {
        requestRetrieved = 0;

        rangeRetrieved = new MutableLiveData<>();
        rangeRetrieved.setValue(0);
    }
    public Integer getRequestRetrieved() {
        if (requestRetrieved == null) {
            requestRetrieved = 0;
        }
        return requestRetrieved;
    }

    public void setRequestRetrieved(Integer retrievedValue) {
        this.requestRetrieved = ++retrievedValue;
    }

    public MutableLiveData<Integer> getRangeRetrieved() {
        if (rangeRetrieved == null) {
            rangeRetrieved = new MutableLiveData<>();
            rangeRetrieved.setValue(0);
        }

        return rangeRetrieved;
    }

    public void postRangeRetrieved(int retrievedValue) {
        this.rangeRetrieved.postValue(retrievedValue);
    }

    public int getMax_range() {
        return max_range;
    }
}