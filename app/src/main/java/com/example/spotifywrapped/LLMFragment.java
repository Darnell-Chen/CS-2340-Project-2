package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

public class LLMFragment extends Fragment {

    WrappedViewModel wrappedVM;
    public LLMFragment() {
        // Required empty public constructor
    }
    public static LLMFragment newInstance() {
        LLMFragment fragment = new LLMFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        new Thread(() -> {
            try {
                String response = wrappedVM.getGPTResponse();

                if (isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        // Now safe to update UI
                        System.out.println(response);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_llm_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.llmBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();


    }
}