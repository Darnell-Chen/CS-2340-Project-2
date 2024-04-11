package com.example.spotifywrapped;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    private WrappedViewModel wrappedVM;

    private View view;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
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
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FrameLayout background = getView().findViewById(R.id.summaryBackground);

        AnimationDrawable animDrawable = (AnimationDrawable) background.getBackground();
        animDrawable.setEnterFadeDuration(2000);
        animDrawable.setExitFadeDuration(2500);
        animDrawable.start();

        wrappedVM = new ViewModelProvider(requireActivity()).get(WrappedViewModel.class);

        this.view = view;

        wrappedVM.getBool().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                getData(view);
            }
        });

    }

    private void getData(View view) {
        getTopArtist();
        getTopGenres();
        getTopSongs();
        getTopAlbums();

        exportImage();
    }

    private void getTopArtist() {
        ArrayList<String> topArtists = wrappedVM.getTopArtist();

        Context context = getActivity();
        for (int i = 1; i <= topArtists.size() && i <= 3; i++) {
            String name = "artist" + i + "TV";
            int id = getResources().getIdentifier(name, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) getView().findViewById(id);
                textView.setText(topArtists.get(i - 1));
            }
        }

        wrappedVM.getTopArtistImg().into((ImageView) view.findViewById(R.id.spotifyImage3));
    }

    private void getTopGenres() {
        ArrayList<String> topGenres = wrappedVM.getTopGenres();

        Context context = getActivity();
        for (int i = 1; i <= topGenres.size() && i <= 3; i++) {
            String name = "genre" + i + "TV";
            int id = getResources().getIdentifier(name, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topGenres.get(i - 1));
                System.out.println(topGenres.get(i - 1));
            }
        }
    }

    private void getTopSongs() {
        ArrayList<Track> topSongList = wrappedVM.getTopSong();
        Context context = getActivity();

        for (int i = 1; i <= topSongList.size() && i <= 3; i++) {
            String songName = "song" + i + "TV";

            int id = getResources().getIdentifier(songName, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topSongList.get(i - 1).getTrackName());
            }

            if (i == 1) {
                String artistImg = topSongList.get(i - 1).getURL();
                RequestCreator img = Picasso.get().load(artistImg).resize(1000, 1000).centerCrop();
                img.into((ImageView) view.findViewById(R.id.spotifyImage2));
            }
        }
    }

    private void getTopAlbums() {
        ArrayList<Track> topAlbumsList = wrappedVM.getTopAlbums();
        Context context = getActivity();

        for (int i = 1; i <= topAlbumsList.size() && i <= 3; i++) {
            String songName = "album" + i + "TV";

            int id = getResources().getIdentifier(songName, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topAlbumsList.get(i - 1).getTrackName());
            }

            if (i == 1) {
                String artistImg = topAlbumsList.get(i - 1).getURL();
                RequestCreator img = Picasso.get().load(artistImg).resize(1000, 1000).centerCrop();
                img.into((ImageView) view.findViewById(R.id.spotifyImage1));
            }
        }
    }

    public void exportImage() {
        FrameLayout summaryBackground = view.findViewById(R.id.summaryBackground);
        summaryBackground.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = ImageExporter.captureLayoutAsBitmap(summaryBackground);
                wrappedVM.addImage(bitmap);
            }
        });
    }


}