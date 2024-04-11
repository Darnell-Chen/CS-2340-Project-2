package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class GamesActivity extends AppCompatActivity {

    Button homeButton;
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
                gameFragment.releaseMediaPlayer();
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