package com.example.spotifywrapped;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class tempDashActivity extends AppCompatActivity {

    private TextView tvToken, tvCode, tvProfile;
    private Button btnToken, btnCode, btnProfile;
    private String mAccessToken;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_page);

        tvToken = findViewById(R.id.TokenTV);
        tvCode = findViewById(R.id.CodeTV);
        tvProfile = findViewById(R.id.ProfileTV);

        btnToken = findViewById(R.id.TokenBTN);
        btnCode = findViewById(R.id.CodeBTN);
        btnProfile = findViewById(R.id.ProfileBTN);

        btnToken.setOnClickListener((v) -> {
            AuthToken newAuth = new AuthToken();
            newAuth.getToken(this);
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
        }
    }
}
