package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.HashMap;

public class LLMFragment extends Fragment {

    WrappedViewModel wrappedVM;
    String GPTResponse;
    public LLMFragment() {
    }
    public static LLMFragment newInstance() {
        LLMFragment fragment = new LLMFragment();
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
        return inflater.inflate(R.layout.fragment_llm_, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.llmBackground);

        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        wrappedVM.getBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (wrappedVM.getFragmentDataRecieved("LLM")) {
                    GPTResponse = wrappedVM.getLLMString();
                    startFragment();
                } else {
                    sendGPTRequest();
                }
            }
        });

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();
    }

    public void sendGPTRequest() {
        new Thread(() -> {
            try {
                String response = wrappedVM.getGPTResponse();

                if (isAdded()) {
                    getActivity().runOnUiThread(() -> {
                        // Now safe to update UI
                        wrappedVM.setFragmentDataRecieved("LLM", true);
                        wrappedVM.setLLMString(response);
                        GPTResponse = response;
                        startFragment();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startFragment() {
        System.out.println(GPTResponse);
    }
}