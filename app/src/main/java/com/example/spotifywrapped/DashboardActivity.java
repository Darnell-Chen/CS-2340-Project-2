package com.example.spotifywrapped;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);
    }

    public void openWrapped(View view) {
        startActivity(new Intent(DashboardActivity.this, WrappedActivity.class));
        finish();
    }

    public void openDuo(View view) {
        System.out.println("going to Duo");
    }

    public void openLLM(View view) {
        System.out.println("going to LLM");
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
