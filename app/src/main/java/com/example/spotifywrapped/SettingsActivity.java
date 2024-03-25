package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvToken, tvCode, tvProfile, tvBack;
    private Button btnToken, btnSongs, btnProfile;
    private String mAccessToken = "";

    private DatabaseReference fbDatabase;
    private FirebaseAuth fbAuth;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        tvToken = findViewById(R.id.TokenTV);
        tvCode = findViewById(R.id.CodeTV);
        tvProfile = findViewById(R.id.ProfileTV);
        tvBack = findViewById(R.id.settingsBack);

        btnToken = findViewById(R.id.TokenBTN);
        btnSongs = findViewById(R.id.getSongsBTN);
        btnProfile = findViewById(R.id.ProfileBTN);

        btnToken.setOnClickListener((v) -> {
            AuthToken newAuth = new AuthToken();
            newAuth.getToken(this);
        });

        btnSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAccessToken.equals("")) {
                    Toast.makeText(SettingsActivity.this, "There's no Auth Token", Toast.LENGTH_SHORT).show();
                } else {
                    SpotifyRequest newRequest = new SpotifyRequest();
                    newRequest.getUserTop(SettingsActivity.this, mAccessToken, "albums", "long_term");
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            tvToken.setText(mAccessToken);

            fbAuth = FirebaseAuth.getInstance();
            fbDatabase = FirebaseDatabase.getInstance().getReference();

            fbDatabase.child("Users").child(fbAuth.getUid().toString()).child("AuthToken").setValue(mAccessToken);

            System.out.println(mAccessToken);
            SpotifyRequest newRequest = new SpotifyRequest();

            String[] requestType = {"artists", "tracks", "albums", "genres"};
            for (int i = 0; i < requestType.length; i++) {
                newRequest.getUserTop(SettingsActivity.this, mAccessToken, requestType[i], "long_term");

                // probably bad practice, but I set a delay here to give time inbetween every api call
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void deleteAccount(View view) {
        DeleteAccount delete = new DeleteAccount(SettingsActivity.this);
        delete.terminateAccount();
    }
}
