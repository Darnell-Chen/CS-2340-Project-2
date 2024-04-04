package com.example.spotifywrapped;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TransitionFragment extends Fragment {

    WrappedViewModel wrappedvm;

    public static TransitionFragment newInstance(String param1, String param2) {
        TransitionFragment fragment = new TransitionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transition, container, false);
    }
}