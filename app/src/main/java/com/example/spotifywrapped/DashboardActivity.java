package com.example.spotifywrapped;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImg;
    private DatabaseReference currReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        profileImg = findViewById(R.id.imageView);
        currReference = FirebaseDatabase.getInstance().getReference();

        //Users id
        DatabaseReference profileRef = currReference.child("Users").child(auth.getUid().toString()).child("profile");


    }

    public void openWrapped(View view) {
        startActivity(new Intent(DashboardActivity.this, InitialWrappedActivity.class));
        finish();
    }

    public void openGames(View view) {
        startActivity(new Intent(DashboardActivity.this, GamesActivity.class));
        finish();
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
}
