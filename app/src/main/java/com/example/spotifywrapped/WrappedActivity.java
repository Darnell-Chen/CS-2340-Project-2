package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class WrappedActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    //private final String[] storyText = {"Screen 1", "Screen 2", "Screen 3", "Screen 4", "Screen 5", "Screen 6"};

    private WrappedViewModel wrappedVM;

    private List<Class<? extends Fragment>> fragments;

    private int numPages;

    long pressTime = 0L;
    long limit = 500L;

    private StoriesProgressView storiesProgressView;
    MediaPlayer mediaPlayer;
    private int counter = 0;

    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.wrapped_layout);


        wrappedVM = new ViewModelProvider(this).get(WrappedViewModel.class);
        wrappedVM.getFirebaseData();

        fragments = asList(TopArtistFragment.class, TopItemsFragment.class,
                TopGenresFragment.class, TopAlbumsFragment.class,
                SummaryFragment.class, LLMFragment.class);

        numPages = fragments.size();

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(fragments.size());
        storiesProgressView.setStoryDuration(9000L);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories(counter);

        playAudio();

        View reverse = findViewById(R.id.reverse);

        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });

        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
        if (counter < numPages) {
            counter++;
            getCorrectFragment(counter);
        } else {
            this.onComplete();
        }
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        --counter;
        getCorrectFragment(counter);
    }

    private void getCorrectFragment(int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragments.get(i), null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();
    }

    private void playAudio() {

        String audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";

        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

}
