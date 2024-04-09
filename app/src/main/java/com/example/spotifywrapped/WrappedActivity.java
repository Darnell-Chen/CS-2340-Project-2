package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class WrappedActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private WrappedViewModel wrappedVM;

    private List<Class<? extends Fragment>> fragments = asList(TopArtistFragment.class, TopItemsFragment.class,
            TopGenresFragment.class, TopAlbumsFragment.class,
            SummaryFragment.class, LLMFragment.class);

    private int numPages;

    long pressTime = 0L;
    long limit = 500L;

    private StoriesProgressView storiesProgressView;
    private ArrayList<String> audioList;
    private MediaPlayer mediaPlayer;
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

        Intent receiverIntent = getIntent();
        String range = receiverIntent.getStringExtra("term");


        wrappedVM = new ViewModelProvider(this).get(WrappedViewModel.class);
        wrappedVM.getFirebaseData(range);

        wrappedVM.getBool().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                audioList = wrappedVM.getAudioList();
                playAudio();

            }
        });

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
    }

    private void playAudio() {

        releaseMediaPlayer();

        String audioUrl = audioList.get(counter);

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

        new AlertDialog.Builder(this)
                .setMessage("Would you like to export an image file of your wrapped summary?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        getSummaryImage(true);
                        showSecondDialog();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSecondDialog();
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
                        storeBitmap(getSummaryImage(false));
                        Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(WrappedActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();
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

    private static void storeBitmap(Bitmap bitmap) {
        DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference currReference
                = fbDatabase.child("Users").child(auth.getUid().toString()).child("profile").child("Summaries");

        ByteArrayOutputStream imageStore = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageStore);
        byte[] imageData = imageStore.toByteArray();
//
//        String base64ImageData = Base64.encodeToString(imageData, Base64.DEFAULT);
//
//        currReference.child("summaryData" + Long.toString(System.currentTimeMillis())).setValue(base64ImageData);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://spotify-wrapped-f4043.appspot.com");

        String currImageName = "summaryData" + Long.toString(System.currentTimeMillis());
        StorageReference currUser = storageRef.child(auth.getUid()).child(currImageName);

        UploadTask uploadTask = currUser.putBytes(imageData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                currUser.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        currReference.child("summaryData" + Long.toString(System.currentTimeMillis())).setValue(url);
                    }
                });
            }
        });
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d(null, "onFailure: upload unsuccessful");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                uploadTask.get
//                currReference.child(currImageName).setValue(currImageName);
//            }
//        });
    }

}
