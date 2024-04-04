package com.example.spotifywrapped;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profileImg;
    private DatabaseReference currReference;
    DataSnapshot profileRef;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);
        FirebaseAuth auth = FirebaseAuth.getInstance();

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
