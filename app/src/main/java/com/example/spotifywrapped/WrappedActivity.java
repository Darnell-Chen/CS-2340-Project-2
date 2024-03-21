package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class WrappedActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    //private final String[] storyText = {"Screen 1", "Screen 2", "Screen 3", "Screen 4", "Screen 5", "Screen 6"};
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private final List<Class<? extends Fragment>> fragments
            = asList(TopArtistFragment.class, TopItemsFragment.class,
            TopGenresFragment.class, TopAlbumsFragment.class,
            SummaryFragment.class, LLMFragment.class);

    private final int numPages = fragments.size();

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        mDatabase.child("Users").child(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(WrappedActivity.this, "Could not retrieve information", Toast.LENGTH_SHORT).show();
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    DataSnapshot dataResult = task.getResult();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                }
            }
        });

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
            System.out.println(counter);
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
