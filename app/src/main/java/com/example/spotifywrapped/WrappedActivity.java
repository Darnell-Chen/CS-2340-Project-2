package com.example.spotifywrapped;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WrappedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_layout);

        WrappedAnimations anim = new WrappedAnimations(this);
        setContentView(anim);


        setContentView(R.layout.wrapped_layout);
    }
}
