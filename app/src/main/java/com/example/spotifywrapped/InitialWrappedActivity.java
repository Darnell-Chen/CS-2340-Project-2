package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InitialWrappedActivity extends AppCompatActivity {

    private Button longBTN, medBTN, shortBTN;
    private TextView backButton;

    private final String[] selectIntro = {"Hi!", "Select an Option to get Started!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_term_page);

        backButton = findViewById(R.id.termSelectBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitialWrappedActivity.this, DashboardActivity.class));
                finish();
            }
        });

        longBTN = findViewById(R.id.longTermBTN);
        medBTN = findViewById(R.id.mediumTermBTN);
        shortBTN = findViewById(R.id.shortTermBTN);

        longBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialWrappedActivity.this, WrappedActivity.class);
                intent.putExtra("term", "long_term");
                startActivity(intent);
                finish();
            }
        });

        medBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialWrappedActivity.this, WrappedActivity.class);
                intent.putExtra("term", "medium_term");
                startActivity(intent);
                finish();
            }
        });

        shortBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialWrappedActivity.this, WrappedActivity.class);
                intent.putExtra("term", "short_term");
                startActivity(intent);
                finish();
            }
        });


    }
}
