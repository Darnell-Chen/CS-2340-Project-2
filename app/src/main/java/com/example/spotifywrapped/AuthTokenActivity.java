package com.example.spotifywrapped;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthTokenActivity extends AppCompatActivity {
    TextView tv;
    Button connectButton;
    String[] stringMessages = {"Welcome", "This Is \n Spotify Unwrapped", "click below to get started"};
    int i = 0;
    AuthViewModel viewmodel;

    private DatabaseReference fbDatabase;
    private FirebaseAuth fbAuth;
    private String mAccessToken = "";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_token);

        viewmodel = new ViewModelProvider(this).get(AuthViewModel.class);

        connectButton = findViewById(R.id.authButton);

        // will implement fade-in for button in final part of project
//        connectButton.setAlpha(0);

        connectButton.setOnClickListener((v) -> {
            AuthToken newAuth = new AuthToken();
            newAuth.getToken(this);
            tv.setText("loading...");
            getFadeInViewPropertyAnimator().start();
        });

        tv = findViewById(R.id.TVAuthToken);
        setText();
        getFadeOutViewPropertyAnimator().start();
    }

    private ViewPropertyAnimator getFadeInViewPropertyAnimator(){
        return tv.animate().alpha(1).setDuration(1500).setStartDelay(750).withEndAction(fadeInEndAction);
    }

    private ViewPropertyAnimator getFadeOutViewPropertyAnimator(){
        return tv.animate().alpha(0).setDuration(1500).setStartDelay(1500).withEndAction(fadeOutEndAction);
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

    private void setText(){
        tv.setText(stringMessages[i++]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

            fbAuth = FirebaseAuth.getInstance();
            fbDatabase = FirebaseDatabase.getInstance().getReference();

            fbDatabase.child("Users").child(fbAuth.getUid().toString()).child("AuthToken").setValue(mAccessToken);

            System.out.println(mAccessToken);
            SpotifyRequest newRequest = new SpotifyRequest();

            String[] requestType = {"artists", "tracks", "albums", "genres"};

            newRequest.getUserTop(AuthTokenActivity.this, mAccessToken, requestType[0], "long_term", viewmodel);

            viewmodel.getRetrieved().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer retrieved) {
                    int nextRequest = (int) viewmodel.getRetrieved().getValue();

                    if (nextRequest == requestType.length) {
                        startActivity(new Intent(AuthTokenActivity.this, DashboardActivity.class));
                        finish();

                    } else {
                        newRequest.getUserTop(AuthTokenActivity.this, mAccessToken, requestType[nextRequest], "long_term", viewmodel);
                    }
                }
            });
        }
    }
}