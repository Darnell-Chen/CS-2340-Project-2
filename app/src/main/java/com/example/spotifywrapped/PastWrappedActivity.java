package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PastWrappedActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private TextView backBTN;

    private RecyclerView summaryRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_wrapped);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + auth.getUid().toString() + "/profile");

        summaryRV = findViewById(R.id.summaryRecycler);
        backBTN = findViewById(R.id.pastWrappedBackBTN);
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PastWrappedActivity.this, DashboardActivity.class));
                finish();
            }
        });

        checkExist();
    }

    private void checkExist() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Summary")) {
                    getSummaries(snapshot.child("Summary").getValue());
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

    private void getSummaries(Object value) {
        HashMap<String, String> summaryMap = (HashMap<String, String>) value;
        ArrayList<String> summaryList = new ArrayList<>();
        summaryList.addAll(summaryMap.keySet());

        // basically gets the keyset and parses them out into Card objects
        ArrayList<Card> summaryCards = new ArrayList<>();
        for (int i = 0; i < summaryList.size(); i++) {
            summaryCards.add(new Card(summaryList.get(i)));
        }
        // just to clear it out :)
        summaryList = null;
        summaryMap = null;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        summaryRV.setLayoutManager(linearLayoutManager);

        SummaryAdapter summaryAdapter = new SummaryAdapter(this, summaryCards, PastWrappedActivity.this);
        summaryRV.setAdapter(summaryAdapter);
    }

    private void NoDataFound() {
        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
    }
}