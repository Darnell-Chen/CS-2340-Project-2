package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PastWrappedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private ArrayList<String> summaryList;
    private TextView backBTN, nextBTN, prevBTN;
    private ImageView currIMG, rickAstley;

    private int currIMGNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_wrapped);

        backBTN = findViewById(R.id.pastWrappedBackBTN);
        setOnClick();
        currIMG = findViewById(R.id.summaryIV);

        nextBTN = findViewById(R.id.nextWrapped);
        prevBTN = findViewById(R.id.prevWrapped);
        initializeButtons();

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + auth.getUid().toString() + "/profile");

        storage = FirebaseStorage.getInstance();

        checkExist();
    }

    private void initializeButtons() {
        nextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (summaryList == null) {
                    Toast.makeText(PastWrappedActivity.this, "nothing to load", Toast.LENGTH_SHORT).show();
                } else {
                    if (currIMGNum < (summaryList.size() - 1)) {
                        currIMGNum += 1;
                        postSummaries(currIMGNum);
                    }
                }
            }
        });

        prevBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (summaryList == null) {
                    Toast.makeText(PastWrappedActivity.this, "This is the end of the list.", Toast.LENGTH_SHORT).show();
                } else {
                    if (currIMGNum > 0) {
                        currIMGNum -= 1;
                        postSummaries(currIMGNum);
                    } else {
                        Toast.makeText(PastWrappedActivity.this, "You're at the start of this list.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setOnClick() {
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PastWrappedActivity.this, InitialWrappedActivity.class));
                finish();
            }
        });
    }

    private void checkExist() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Summaries")) {
                    getSummaries();
                } else {
                    System.out.println("No Summaries Found");
                    NoDataFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("No Summaries Found");
                NoDataFound();
            }
        });
    }

    private void getSummaries() {
        System.out.println("summaries found");

        DatabaseReference summaryRef = mDatabase.child("Summaries");
        summaryRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                HashMap<String, String> myResult = (HashMap<String, String>) task.getResult().getValue();
                parseSummaries(myResult);
                currIMGNum = 0;
                postSummaries(currIMGNum);
            }
        });

    }

    private void parseSummaries(HashMap<String, String> myResult) {
        summaryList = new ArrayList<>();
        summaryList.addAll(myResult.values());
    }

    private void NoDataFound() {
    }

    private void postSummaries(int i) {
        String currIMGLink = summaryList.get(i);

        Picasso.get()
                .load(currIMGLink)
                .into(currIMG);
    }
}