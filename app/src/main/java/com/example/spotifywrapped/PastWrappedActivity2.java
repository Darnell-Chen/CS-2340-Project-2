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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class PastWrappedActivity2 extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    /*private final List<Class<? extends Fragment>> fragments
            = asList(TopArtistFragment.class, TopItemsFragment.class,
            TopGenresFragment.class, TopAlbumsFragment.class,
            SummaryFragment.class, LLMFragment.class);*/

    private int numPages;

    long pressTime = 0L;
    long limit = 700L;


    private StoriesProgressView storiesProgressView;

    private int counter = 0;
    private ArrayList<String> summaryList = new ArrayList<>();
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ImageView currIMG;

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
        setContentView(R.layout.activity_past_wrapped2);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("numNodes")) {
            numPages = intent.getIntExtra("numNodes", 0);
        } else {
            numPages = 0;
        }
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + auth.getUid().toString() + "/profile");
        currIMG = findViewById(R.id.summaryImage);
        getSummaries();

        /*storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(numPages);
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
        skip.setOnTouchListener(onTouchListener);*/
    }

    @Override
    public void onNext() {
        if (counter < numPages) {
            counter++;
            postSummaries(counter);
            System.out.println(counter);
        } else {
            this.onComplete();
        }
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        --counter;
        postSummaries(counter);
    }

    @Override
    public void onComplete() {
        Intent i = new Intent(PastWrappedActivity2.this, DashboardActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void getSummaries() {
        DatabaseReference summaryRef = mDatabase.child("Summaries");
        summaryRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, String> myResult = (HashMap<String, String>) task.getResult().getValue();
                parseSummaries(myResult);
                Log.d("summary list size", "summary size " + Integer.toString(summaryList.size()));
                for (int i = 0; i < summaryList.size(); i++) {
                    Log.d("summary list", "Element " + i + " " + summaryList.get(i));
                };
                if (!summaryList.isEmpty()) {
                    storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
                    storiesProgressView.setStoriesCount(numPages);
                    storiesProgressView.setStoryDuration(9000L);
                    storiesProgressView.setStoriesListener(PastWrappedActivity2.this);
                    storiesProgressView.startStories(counter);
                    postSummaries(counter);

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
            }
        });

    }

    private void parseSummaries(HashMap<String, String> myResult) {
        summaryList = new ArrayList<>();
        summaryList.addAll(myResult.values());
    }

    private void postSummaries(int i) {
        String currIMGLink = summaryList.get(i);

        Picasso.get()
                .load(currIMGLink)
                .into(currIMG);
    }
}