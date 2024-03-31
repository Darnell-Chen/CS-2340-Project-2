package com.example.spotifywrapped;

import static java.util.Arrays.asList;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class GamesActivity extends AppCompatActivity {

    Button homeButton, startButton;
    WrappedViewModel vm;

    // this is the fragment that holds our actual game
    private GameSecondFragment gameFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_games);
        gameFragment = new GameSecondFragment();

        homeButton = findViewById(R.id.homeBTN);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GamesActivity.this, DashboardActivity.class));
                finish();
            }
        });

        vm = new ViewModelProvider(this).get(WrappedViewModel.class);
        vm.getFirebaseData("long_term");
    }

    public void switchFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.gameFragmentContainer, gameFragment)
                .addToBackStack(null)
                .commit();
    }
}