package com.example.spotifywrapped;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
public class TopItemsFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.topSongsBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();

    }
}