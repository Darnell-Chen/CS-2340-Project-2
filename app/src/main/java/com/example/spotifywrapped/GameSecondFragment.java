package com.example.spotifywrapped;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private ArrayList<Track> songList, songPool;
    private TextView currScore, highScore, prompt;
    private Button option1, option2, option3, option4, homeButton;
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
        removeNullTracks();
        songPool = new ArrayList<Track>();
        choices = new Track[4];
        getScreenItems(ourView);
        resetScore();
        promptUser();
    }

    private void removeNullTracks() {
        ArrayList<Track> cleanedTracks = new ArrayList<>();

        for (Track x: songList) {
            if (!x.getURL().equals("null")) {
                cleanedTracks.add(x);
            }
        }

        songList = cleanedTracks;
    }

    private void resetScore() {
        currScore.setText("0");
    }

    private void getScreenItems(View view) {
        currScore = view.findViewById(R.id.currentScoreTV2);
        highScore = view.findViewById(R.id.highScoreTV);
        prompt = view.findViewById(R.id.promptTV);

        option1 = view.findViewById(R.id.choiceBTN1);
        option2 = view.findViewById(R.id.choiceBTN2);
        option3 = view.findViewById(R.id.choiceBTN3);
        option4 = view.findViewById(R.id.choiceBTN4);
        homeButton = getActivity().findViewById(R.id.homeBTN);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(0));
                highlightChoice(option1, correctChoice.equals(choices[0]));
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(1));
                highlightChoice(option2, correctChoice.equals(choices[1]));
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(2));
                highlightChoice(option3, correctChoice.equals(choices[2]));
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRoundResult(checkChoice(3));
                highlightChoice(option4, correctChoice.equals(choices[3]));
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
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);

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

            ourView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Reset button colors
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));

                    option1.setEnabled(true);
                    option2.setEnabled(true);
                    option3.setEnabled(true);
                    option4.setEnabled(true);

                    // Proceed to next song
                    promptUser();
                }
            }, 1000);
        } else {
            // TERMINATE GAME
            /* Pop up in the middle of the screen which tells the user that they lost, what their score was,
            and the high score they need to beat. */
            ourView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Reset button colors
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.argb(64, 0, 0, 0)));

                    option1.setEnabled(true);
                    option2.setEnabled(true);
                    option3.setEnabled(true);
                    option4.setEnabled(true);

                    // Proceed to next song
                    beginGame();
                }
            }, 1000);
        }
    }

    private Track selectTrack(ArrayList<Track> pool) {
        Random rand = new Random();
        int sel = rand.nextInt(pool.size());
        Track currTrack = pool.get(sel);
        pool.remove(sel);

        // we can't return pool.remove(sel) - likely b/c it doesnt process immediately
        // causing overlap in choices
        return currTrack;
    }

    private void resetPool() {
        songPool.clear();
        songPool.addAll(songList);
    }

    private void playAudio(String currURL) {

        releaseMediaPlayer();

        // initializing media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);

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

    public void releaseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void highlightChoice(Button button, boolean isCorrect) {
        if (isCorrect) {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }
    }
}