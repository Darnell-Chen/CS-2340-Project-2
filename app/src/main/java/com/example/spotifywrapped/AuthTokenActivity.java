package com.example.spotifywrapped;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

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
        viewmodel.addButton(connectButton);


        tv = findViewById(R.id.TVAuthToken);
        setText();
        getFadeOutViewPropertyAnimator().start();

        connectButton.setOnClickListener((v) -> {
            AuthToken newAuth = new AuthToken();
            newAuth.getToken(this);
            tv.setText("loading...");
            getFadeInViewPropertyAnimator().start();
            connectButton.setEnabled(false);
        });
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
        if (connectButton.isEnabled()) {
            tv.setText(stringMessages[i++]);
        }
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

            SpotifyRequest newRequest = new SpotifyRequest();
            newRequest.setViewModel(viewmodel);

            String[] requestType = {"artists", "tracks", "albums", "genres", "profile"};
            String[] rangeType = {"long_term", "medium_term", "short_term"};

            newRequest.getUserTop(AuthTokenActivity.this, mAccessToken, requestType[0], rangeType[0], viewmodel);

            viewmodel.getRangeRetrieved().observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer retrieved) {
                    int nextRequest = (int) viewmodel.getRequestRetrieved();
                    int nextRange = (int) viewmodel.getRangeRetrieved().getValue();

                    if (nextRequest >= (requestType.length - 1) && nextRange >= 0) {
                        if (nextRange == 0) {
                            // grabs user profile
                            newRequest.getUserTop(AuthTokenActivity.this, mAccessToken, requestType[nextRequest], rangeType[0], viewmodel);
                        } else {
                            startActivity(new Intent(AuthTokenActivity.this, DashboardActivity.class));
                            finish();
                        }

                    } else {
                        newRequest.getUserTop(AuthTokenActivity.this, mAccessToken, requestType[nextRequest], rangeType[nextRange], viewmodel);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please check Spotify Client ID and Redirect URI", Toast.LENGTH_SHORT).show();
            connectButton.setEnabled(true);
        }
    }
}