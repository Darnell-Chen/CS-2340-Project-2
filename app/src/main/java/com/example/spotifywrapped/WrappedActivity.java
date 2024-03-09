package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

public class WrappedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_layout);

        startAnimation();

        // This will display the wrapped fragment container
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.wrappedFragmentContainer, TopItemsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // Name can be null
                .commit();
    }

    private void startAnimation() {
        ConstraintLayout background = findViewById(R.id.wrappedBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2500);
        animDrawable.setExitFadeDuration(3000);
        animDrawable.start();
    }
}
