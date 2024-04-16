package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class InitialWrappedActivity extends AppCompatActivity {

    private Button longBTN, medBTN, shortBTN;
    private TextView backButton;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private int numNodes;

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
        //summaryBTN = findViewById(R.id.ViewPastButton);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + auth.getUid().toString() + "/profile");

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
        /*summaryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkExist();
            }
        });*/

    }

    private void checkExist() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Summaries")) {
                    DataSnapshot summariesSnapshot = snapshot.child("Summaries");
                    int numNodes = (int) summariesSnapshot.getChildrenCount();
                    if (numNodes > 0) {
                        Intent intent = new Intent(InitialWrappedActivity.this, PastWrappedActivity2.class);
                        intent.putExtra("numNodes", numNodes);
                        startActivity(intent);
                        finish();
                    } else {
                        NoDataFound();
                    }
                } else {
                    NoDataFound();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                NoDataFound();
            }
        });
    }


    private void NoDataFound() {
        Toast.makeText(this, "No summaries found", Toast.LENGTH_SHORT).show();

    }
}
