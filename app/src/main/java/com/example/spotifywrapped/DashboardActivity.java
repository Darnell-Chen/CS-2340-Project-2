package com.example.spotifywrapped;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImg;
    private DatabaseReference currReference;
    DataSnapshot profileRef;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users/" + auth.getUid().toString() + "/profile");

        profileImg = findViewById(R.id.imageView);
        currReference = FirebaseDatabase.getInstance().getReference();

        //Users id
//        DatabaseReference profileRef = currReference.child("Users").child(auth.getUid().toString()).child("profile");

        currReference.child("Users").child(auth.getUid()).child("profile").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    profileRef = task.getResult();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    setProfile();
                }
            }
        });
    }

    private void setProfile() {
        String userImage = profileRef.child("image").getValue().toString();
        String userName = profileRef.child("name").getValue().toString();

        RequestCreator image = transformImage(userImage);

        image.into(profileImg);
    }

    public RequestCreator transformImage(String artistImg) {
        return Picasso.get().load(artistImg).resize(1000, 1000).centerCrop();
    }

    public void openWrapped(View view) {
        startActivity(new Intent(DashboardActivity.this, InitialWrappedActivity.class));
        finish();
    }

    public void openGames(View view) {
        checkExist();
        //startActivity(new Intent(DashboardActivity.this, PastWrappedActivity.class));
        //finish();
    }

    public void openSettings(View view) {
        startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
        finish();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        finish();
    }

    private void checkExist() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Summary")) {
                    startActivity(new Intent(DashboardActivity.this, PastWrappedActivity.class));
                    finish();
                }  else {
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
