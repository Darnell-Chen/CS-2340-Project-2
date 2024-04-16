package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class WrappedActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private WrappedViewModel wrappedVM;

    private List<Class<? extends Fragment>> fragments = asList(
            TransitionFragment.class, TransitionFragment.class,
            TopArtistFragment.class, TransitionFragment.class,
            TopItemsFragment.class, TransitionFragment.class,
            TopGenresFragment.class, TransitionFragment.class,
            TopAlbumsFragment.class, TransitionFragment.class,
            SummaryFragment.class, LLMFragment.class);

    private int numPages;
    long pressTime = 0L;
    long limit = 700L;

    private StoriesProgressView storiesProgressView;
    private ArrayList<String> audioList;
    private MediaPlayer mediaPlayer;
    private int counter = 0;

    private boolean isSummary;

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

        Intent receiverIntent = getIntent();
        String range = receiverIntent.getStringExtra("term");

        wrappedVM = new ViewModelProvider(this).get(WrappedViewModel.class);
        wrappedVM.getFirebaseData(range);

        // I hope no one ever sees this line of code. It tells you whether this is a summary or a regular wrapped.
        if (range.length() > 15) {
            isSummary = true;
        } else {
            isSummary = false;
        }

        wrappedVM.getBool().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                audioList = wrappedVM.getAudioList();
                playAudio();

                getLLM();
            }
        });

        WrappedMiscellaneous.setCounter(0);

        numPages = fragments.size();

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(fragments.size());
        storiesProgressView.setStoryDuration(9000L);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories(counter);

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

    private void getLLM() {
        if (wrappedVM.getFragmentDataRecieved("LLM")) {
            Log.d(null, "getLLM: LLM already recieved");
        } else {
            sendGPTRequest();
        }
    }

    @Override
    public void onNext() {
        if (counter < numPages) {
            int next = counter + 1;
            getCorrectFragment(next);
            if (audioList != null) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                playAudio();
            }
        } else {
            if (mediaPlayer != null) {
                releaseMediaPlayer();
            }
            this.onComplete();
        }
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        int prev = counter - 1;
        getCorrectFragment(prev);

        if (audioList != null) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            playAudio();
        }
    }

    private void getCorrectFragment(int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        boolean transactionForward = i > counter;

        int enterAnimation = transactionForward ? R.anim.enter_right_to_left : R.anim.enter_left_to_right;
        int exitAnimation = transactionForward ? R.anim.exit_right_to_left : R.anim.exit_left_to_right;

        fragmentManager.beginTransaction()
                .setCustomAnimations( enterAnimation, exitAnimation)
                .replace(R.id.fragmentContainerView, fragments.get(i), null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();

        counter = i;
        WrappedMiscellaneous.setCounter(i);
    }

    private void playAudio() {

        releaseMediaPlayer();

        String audioUrl = audioList.get(counter);

        // initializing media player
        mediaPlayer = new MediaPlayer();


        String rick = getString(R.string.rickroll);


        int randomCount = 0;
        while (audioUrl.equals("null")) {
            Random random = new Random();
            audioUrl = audioList.get(random.nextInt(audioList.size() - fragments.size()) + fragments.size() - 1);

            randomCount+=1;
            if (randomCount > 5) {
                audioUrl = rick;
            }
        }

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

        new AlertDialog.Builder(this)
                .setMessage("Would you like to export an image file of your wrapped summary?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getSummaryImage(true);
                        if ((isSummary == false)) {
                            showSecondDialog();
                        } else {
                            endActivity(DashboardActivity.class);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((isSummary == false)) {
                            showSecondDialog();
                        } else {
                            endActivity(DashboardActivity.class);
                        }
                    }
                }).show();
    }

    private void showSecondDialog() {
        new AlertDialog.Builder(WrappedActivity.this)
                .setMessage("Would you like to save your wrapped summary to be viewed later?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        wrappedVM.storeWrapped();
                        showThirdDialog();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showThirdDialog();
                    }
                }).show();
    }

    public void showThirdDialog() {
        new AlertDialog.Builder(WrappedActivity.this)
                .setMessage("Would you like to play a mini-game?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        endActivity(GamesActivity.class);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endActivity(DashboardActivity.class);
                    }
                }).show();
    }


    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        releaseMediaPlayer();
        super.onDestroy();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public Bitmap getSummaryImage(boolean export) {
        Bitmap summaryImage = wrappedVM.getScreenshots().get(0);
        if (export) {
            boolean exported = ImageExporter.saveBitmapToGallery(this, summaryImage,
                    "summary_image", "Image exported from layout");
            if (exported) {
                Toast.makeText(this, "Image exported successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to export image", Toast.LENGTH_SHORT).show();
            }
        }
        return summaryImage;
    }

    public void endActivity(Class<?> activityClass) {
        Intent i = new Intent(WrappedActivity.this, activityClass);
        startActivity(i);
        finish();
    }

    public void sendGPTRequest() {
        new Thread(() -> {
            try {
                String response = wrappedVM.getGPTResponse();

                this.runOnUiThread(() -> {
                    // Now safe to update UI
                    wrappedVM.setFragmentDataRecieved("LLM", true);
                    wrappedVM.setLLMString(response);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }



}
