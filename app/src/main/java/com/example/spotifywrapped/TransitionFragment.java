package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Random;


public class TransitionFragment extends Fragment {

    WrappedViewModel wrappedVM;
    String term;

    String[] stringMessages;
    TextView tv;
    int i = 0;

    private final int total_duration = 5000;
    private int fade_time;
    private int fade_delay;

    public static TransitionFragment newInstance() {
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
        beginAnim(view);

        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        term = getTerm();
        stringMessages = getStringMessages();

        fade_time = (total_duration) / stringMessages.length;
        fade_delay = (fade_time / 2);
        fade_time -= fade_delay;

        tv = getView().findViewById(R.id.TransitionTV);
        setText();
        getFadeOutViewPropertyAnimator().start();
    }

    private void beginAnim(View view) {
        FrameLayout background = getView().findViewById(R.id.transitionBackground);
        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();

        view.findViewById(R.id.miniFrame1).setRotationY(getRandom());
        view.findViewById(R.id.miniFrame2).setRotationY(getRandom());
        view.findViewById(R.id.miniFrame3).setRotationY(getRandom());
        view.findViewById(R.id.miniFrame1).setRotationX(getRandom());
        view.findViewById(R.id.miniFrame2).setRotationX(getRandom());
        view.findViewById(R.id.miniFrame3).setRotationX(getRandom());

        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        Animation rotation2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotation2);
        Animation rotation3 = AnimationUtils.loadAnimation(getContext(), R.anim.rotation3);
        view.findViewById(R.id.miniFrame1).startAnimation(rotation);
        view.findViewById(R.id.miniFrame2).startAnimation(rotation2);
        view.findViewById(R.id.miniFrame3).startAnimation(rotation3);
    }

    private int getRandom() {
        Random rand = new Random();
        return rand.nextInt(720) - 360;
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

    private String getTerm() {
        String range = WrappedMiscellaneous.getTerm();

        if (range == "long_term") {
            return "Year";
        }
        else if (range == "medium_term") {
            return "Semester";
        }
        else {
            return "Quarter";
        }
    }

    private void setText(){
        tv.setText(stringMessages[i++]);
    }

    private String[] getStringMessages() {

        String[] Intro = {"Welcome to Spotify unWrapped",
                "You've Had Quite the Adventure So Far", "So Let's Check Out Your Journey So Far"};

        String[] TopArtist = {"You've Explored a Ton of Artists", "Some Artists Captivated Your Heart More than Others",
                "Here's Your Your Top Dogs."};

        String[] TopSong = {"Your Top Songs are Straight Fire.", "They're Bangers You Can't Forget.",
                "Here's a Peek into Heaven-"};

        String[] TopGenre = {"Your musical tapestry was woven with many diverse genres.",
                "These genres refined your tastes and playlists",
                "So let's Unveil the genres that defined your " + term};

        String[] TopAlbums = {"Some People's Albums Are Trash", "But Not Yours.", "Here I Present... \n Your Holy Scriptures"};

        String[] Summary = {"Here's everything we have seen so far...", "summarized just for you."};

        int i = WrappedMiscellaneous.getCounter();
        switch(i) {
            case 0:
                return Intro;
            case 1:
                return TopArtist;
            case 3:
                return TopSong;
            case 5:
                return TopGenre;
            case 7:
                return TopAlbums;
            default:
                return Summary;
        }
    }
}