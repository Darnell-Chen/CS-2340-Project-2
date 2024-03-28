package com.example.spotifywrapped;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TopAlbumsFragment extends Fragment {

    private WrappedViewModel wrappedVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_albums, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.topAlbumBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();

        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        wrappedVM.getBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                getData(view);
            }
        });
    }

    private void getData(View view) {
        ArrayList<Track> topAlbumList = wrappedVM.getTopAlbums();
    }
}