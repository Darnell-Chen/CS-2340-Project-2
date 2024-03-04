package com.example.spotifywrapped;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvToken;
    private TextView tvCode;
    private TextView tvProfile;
    private Button btnToken;
    private Button btnCode;
    private Button btnProfile;
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
    }
}
