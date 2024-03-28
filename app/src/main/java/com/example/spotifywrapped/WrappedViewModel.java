package com.example.spotifywrapped;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;

public class WrappedViewModel extends ViewModel {

    private MutableLiveData<Boolean> dataReceived = new MutableLiveData<Boolean>();
    private DataSnapshot dataResult;

    public void getFirebaseData(String range) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase.child("Users").child(auth.getUid()).child(range).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    dataResult = task.getResult();
                    dataReceived.setValue(true);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    public ArrayList<String> getTopArtist() {
        DataSnapshot topArtistSnapshot = dataResult.child("top artists");
        HashMap<String, String> newList = (HashMap<String, String>) topArtistSnapshot.getValue();

        ArrayList<String> topArtistList = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            topArtistList.add(topArtistSnapshot.child("artist" + i).child("artist").getValue().toString());
        }

        return topArtistList;
    }

    public RequestCreator getTopArtistImg() {
        DataSnapshot topArtistSnapshot = dataResult.child("top artists").child("artist0").child("url");
        String artistImg = topArtistSnapshot.getValue().toString();
        RequestCreator image = transformImage(artistImg);
        return image;
    }

    private RequestCreator transformImage(String artistImg) {
        return Picasso.get().load(artistImg).resize(1000, 1000).centerCrop();
    }

    public ArrayList<String> getAudioList() {
        DataSnapshot topAudioSnapshot = dataResult.child("top audios");
        HashMap<String, String> newList = (HashMap<String, String>) topAudioSnapshot.getValue();

        ArrayList<String> topAudioList = new ArrayList<>();
        for (int i = 0; i < newList.size(); i++) {
            topAudioList.add(newList.get("audio" + i));
        }

        System.out.println(topAudioList);
        return topAudioList;
    }

    public void getTopSong() {
        DataSnapshot topAlbumSnapshot = dataResult.child("top songs");
    }

    public ArrayList<Track> getTopAlbums() {
        DataSnapshot topAlbumSnapshot = dataResult.child("top albums");

        int snapshotSize = (int) topAlbumSnapshot.getChildrenCount();

        ArrayList<Track> albumList = new ArrayList<>();

        for (int i = 0; (i < snapshotSize) && (i < 5); i++) {
            DataSnapshot currSnapshot = topAlbumSnapshot.child("album" + i);
            String currAlbumName = (String) currSnapshot.child("album").getValue(String.class);
            String currArtist = (String) currSnapshot.child("artist").getValue(String.class);
            String currImage = (String) currSnapshot.child("url").getValue(String.class);

            albumList.add(new Track(currArtist, currAlbumName, currImage));
        }

        return albumList;
    }

    public LiveData<Boolean> getBool() {
        return dataReceived;
    }
}
