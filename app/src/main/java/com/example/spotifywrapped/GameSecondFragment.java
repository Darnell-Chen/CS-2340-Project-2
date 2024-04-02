package com.example.spotifywrapped;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameSecondFragment extends Fragment {

    private WrappedViewModel vm;
    private ArrayList<Track> songList;
    private ArrayList<Track> songPool;
    private TextView currScore, highScore, prompt;
    private Button option1, option2, option3, option4;
    private Track correctChoice;
    private Track[] choices;
    private MediaPlayer mediaPlayer;
    private View ourView;
    public GameSecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_second, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);
        vm.getBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                songList = vm.getGameTracks();
                ourView = view;
                beginGame();
            }
        });
    }

    private void beginGame() {
        songPool = new ArrayList<Track>();
        choices = new Track[4];
        getScreenItems(ourView);
        promptUser();
    }

    private void getScreenItems(View view) {
        currScore = view.findViewById(R.id.currentScoreTV2);
        highScore = view.findViewById(R.id.highScoreTV);
        prompt = view.findViewById(R.id.promptTV);

        option1 = view.findViewById(R.id.choiceBTN1);
        option2 = view.findViewById(R.id.choiceBTN2);
        option3 = view.findViewById(R.id.choiceBTN3);
        option4 = view.findViewById(R.id.choiceBTN4);
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(0));
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(1));
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(2));
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(3));
            }
        });

    }

    private void promptUser() {
        ArrayList<Track> choicePool = new ArrayList<Track>();
        resetPool();

        correctChoice = selectTrack(songPool);
        choicePool.add(correctChoice);

        for (int i = 0; i < 3; i++) {
            choicePool.add(selectTrack(songPool));
        }

        choices[0] = selectTrack(choicePool);
        choices[1] = selectTrack(choicePool);
        choices[2] = selectTrack(choicePool);
        choices[3] = selectTrack(choicePool);

        option1.setText(choices[0].getTrackName());
        option2.setText(choices[1].getTrackName());
        option3.setText(choices[2].getTrackName());
        option4.setText(choices[3].getTrackName());

        prompt.setText("Listen.");

        playAudio(correctChoice.getURL());
    }

    private boolean checkChoice(int ind) {
        return choices[ind].equals(correctChoice);
    }

    private void handleRoundResult(boolean won) {
        if (won) {
            String[] highScoreSubs = highScore.getText().toString().split(" ");
            int newScore = Integer.parseInt(currScore.getText().toString()) + 1;
            int newHighScore = Integer.parseInt(highScoreSubs[2]);
            currScore.setText(String.valueOf(newScore));

            if (newScore > newHighScore) {
                newHighScore++;
                highScore.setText("High Score: " + newHighScore);

                /* Could add a wait period during which the prompt text changes to say
                 "New High Score!". */
            }

            promptUser();
        } else {
            // TERMINATE GAME
            /* Pop up in the middle of the screen which tells the user that they lost, what their score was,
            and the high score they need to beat. */
            // RESET SCORES
            beginGame();
        }
    }

    private Track selectTrack(ArrayList<Track> pool) {
        Random rand = new Random();
        int sel = rand.nextInt(pool.size());
        return pool.remove(sel);
    }

    private void resetPool() {
        songPool.clear();
        songPool.addAll(songList);
    }

    private void playAudio(String currURL) {

        releaseMediaPlayer();

        // initializing media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(currURL);
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}