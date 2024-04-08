package com.example.spotifywrapped;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;


public class TransitionFragment extends Fragment {

    WrappedViewModel wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);
    String term = wrappedVM.getTerm();
    String[] Intro = {"Spotify Unwrapped.", "Welcome to Spotify UnWrapped",
            "You've had Quite the Adventure this " + term, "So Let's Hop Straight into Your Musical Journey"};
    String[] TopArtist = {"You've Explored a Ton of Artists", "Some Artists Captivated Your Heart More than Others",
            "Here Are The Ones You Just Couldn't Stop Listening To"};
    String[] TopSong = {"Your Top Songs, Your Soundtrack.", "The tunes you couldn't forget.",
            "Here's your " + term + " in music."};
    String[] TopGenre = {"Your musical tapestry was woven with many diverse genres.",
            "These genres refined your tastes and playlists",
            "So let's Unveil the genres that defined your " + term};
    String[] TopAlbums = {};

    String[] Summary = {"As the curtains draw, let's point the spotlight back again",
            "Here's everything we have seen so far...", "summarized just for you."};

    String[] stringMessages;
    TextView tv;
    int i = 0;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);
        String term = wrappedVM.getTerm();
    }

    private ViewPropertyAnimator getFadeInViewPropertyAnimator(){
        return tv.animate().alpha(1).setDuration(1500).setStartDelay(750).withEndAction(fadeInEndAction);
    }

    private ViewPropertyAnimator getFadeOutViewPropertyAnimator(){
        return tv.animate().alpha(0).setDuration(1500).setStartDelay(1500).withEndAction(fadeOutEndAction);
    }

    private final Runnable fadeInEndAction = new Runnable() {
        @Override
        public void run() {
            //no more strings to show stop the fade-in/out loop here
            if(i == stringMessages.length){
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
        tv.setText(stringMessages[i++]);
    }
}