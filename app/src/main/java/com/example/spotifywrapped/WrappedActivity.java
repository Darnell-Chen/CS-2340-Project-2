package com.example.spotifywrapped;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikhaellopez.circularimageview.CircularImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class WrappedActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private final String[] storyText = {"Screen 1", "Screen 2", "Screen 3", "Screen 4"};
    private final int numPages = 3;

    long pressTime = 0L;
    long limit = 500L;

    private StoriesProgressView storiesProgressView;
    private TextView storyTTV;

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
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(storyText.length);
        storiesProgressView.setStoryDuration(3000L);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories(counter);

        storyTTV = findViewById(R.id.storyTTV);

        glideImage(storyText[counter]);

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
            glideImage(storyText[counter]);
            getNextFragment(counter);
            System.out.println(counter);
        } else {
            this.onComplete();
        }
    }

    private void getNextFragment(int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.wrappedFragmentContainer, TopItemsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        --counter;
        glideImage(storyText[counter]);
        getPreviousFragment(counter);
    }

    private void getPreviousFragment(int i) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.wrappedFragmentContainer, TopItemsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();
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

    private void glideImage(String storyText)
    {
        storyTTV.setText(storyText);
    }
}
