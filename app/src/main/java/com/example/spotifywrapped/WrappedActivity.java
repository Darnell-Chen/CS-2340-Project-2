package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


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

    @Override
    public void onComplete() {
        new AlertDialog.Builder(this)
                .setMessage("Would you like to export an image file of your wrapped summary?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        getSummaryImage();
                        Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).show();
        //getSummaryImage();
        //Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
        //startActivity(i);
        //finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    public void getSummaryImage() {
        Bitmap summaryImage = wrappedVM.getScreenshots().get(0);
        boolean exported = ImageExporter.saveBitmapToGallery(this, summaryImage,
                "summary_image", "Image exported from layout");
        if (exported) {
            Toast.makeText(this, "Image exported successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to export image", Toast.LENGTH_SHORT).show();
        }
    }
}