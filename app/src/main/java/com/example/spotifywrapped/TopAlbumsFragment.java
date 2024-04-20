package com.example.spotifywrapped;

import android.content.Context;
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

        Context context = getActivity();
        for (int i = 1; i <= topAlbumList.size(); i++) {
            String albumName = "albumNameTV" + i;
            String artistName = "albumArtistTV" + i;
            String albumIMG = "albumIV" + i;

            int id = getResources().getIdentifier(albumName, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topAlbumList.get(i - 1).getTrackName());
            }

            id = getResources().getIdentifier(artistName, "id", context.getPackageName());
            if (id != 0) {
                TextView textView = (TextView) view.findViewById(id);
                textView.setText(topAlbumList.get(i - 1).getArtistName());
            }

            id = getResources().getIdentifier(albumIMG, "id", context.getPackageName());
            if (id != 0) {
                ImageView imageView = (ImageView) view.findViewById(id);
                wrappedVM.transformImage(topAlbumList.get(i - 1).getURL()).into(imageView);
            }
        }
    }
}