package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LLMFragment extends Fragment {

    WrappedViewModel wrappedVM;
    String GPTResponse;
    String[] sentences;
    int i = 0;

    int total_duration = 5000;
    int fade_time;
    int fade_delay;
    TextView tv;
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
        sentences = GPTResponse.split("\\.");
        for (int i = 0; i < sentences.length; i++) {
            Log.d("sentences", sentences[i]);
        }
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(sentences));
        arrayList.remove(0);
        arrayList.remove(0);
        arrayList.add(0, "Here's what your taste has to say about you");
        arrayList.remove(arrayList.size() - 1);
        sentences = arrayList.toArray(new String[0]);

        fade_time = (total_duration) / sentences.length;
        fade_delay = (fade_time / 2);
        fade_time -= fade_delay;

        tv = getView().findViewById(R.id.llmText);
        setText();
        getFadeOutViewPropertyAnimator().start();
    }

    private ViewPropertyAnimator getFadeInViewPropertyAnimator(){
        return tv.animate().alpha(1).setDuration(fade_time).setStartDelay(fade_delay).withEndAction(fadeInEndAction);
    }

    private ViewPropertyAnimator getFadeOutViewPropertyAnimator(){
        return tv.animate().alpha(0).setDuration(fade_time).setStartDelay(fade_delay).withEndAction(fadeOutEndAction);
    }

    private final Runnable fadeInEndAction = new Runnable() {
        @Override
        public void run() {
            //no more strings to show stop the fade-in/out loop here
            if(i == sentences.length){
                return;
            }
            getFadeOutViewPropertyAnimator().start();
        }
    };

    private final Runnable fadeOutEndAction = new Runnable() {
        @Override
        public void run() {
            setText();
            getFadeInViewPropertyAnimator().start();
        }
    };

    private void setText(){
        tv.setText(sentences[i++]);
    }
}