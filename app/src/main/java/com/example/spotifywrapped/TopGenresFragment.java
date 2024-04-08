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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

public class TopGenresFragment extends Fragment {

    private WrappedViewModel wrappedVM;

    public TopGenresFragment() {
        // Required empty public constructor
    }
    public static TopGenresFragment newInstance() {
        TopGenresFragment fragment = new TopGenresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_genres, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.topGenreBackground);

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
        ArrayList<String> topGenres = wrappedVM.getTopGenres();

        Context context = getActivity();
        for (int i = 1; i <= topGenres.size(); i++) {
            String name = "genre" + i;
            int id = getResources().getIdentifier(name, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topGenres.get(i - 1));
            }
        }
    }
}