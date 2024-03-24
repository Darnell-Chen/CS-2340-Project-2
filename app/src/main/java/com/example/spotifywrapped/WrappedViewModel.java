package com.example.spotifywrapped;

import static android.app.PendingIntent.getActivity;

import android.util.Log;
import android.widget.Toast;

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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class WrappedViewModel extends ViewModel {

    private MutableLiveData<Boolean> dataReceived = new MutableLiveData<Boolean>();
    private DataSnapshot dataResult;

    public void getFirebaseData() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mDatabase.child("Users").child(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
            topArtistList.add(newList.get("artist" + i));
        }

        return topArtistList;
    }

    public LiveData<Boolean> getBool() {
        return dataReceived;
    }
}
