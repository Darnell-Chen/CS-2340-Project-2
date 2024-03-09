package com.example.spotifywrapped;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONParser {
    public static void parseTopArtist(JSONObject jObject) throws JSONException {

        JSONArray jsonArtists = jObject.getJSONArray("items");

        ArrayList<String> artistList = new ArrayList<>();

        for (int i = 0; i < jsonArtists.length(); i++) {
            JSONObject currArtist = jsonArtists.getJSONObject(i);
            artistList.add(currArtist.getString("name"));
        }

        storeList("artist", artistList);
    }

    private static void storeList(String key, ArrayList<String> value) {
        DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String newChild = "top ".concat(key).concat("s");

        DatabaseReference currReference = fbDatabase.child("Users").child(auth.getUid().toString()).child(newChild);

        if (key.equals("artist")) {
            for (int i = 0; i < value.size(); i++) {
                currReference.child(key.concat(Integer.toString(i))).setValue(value.get(i));
            }
        } else {
            for (int i = 0; i < value.size()/2; i++) {
                String currSong = value.get(i * 2);
                String currArtist = value.get((i*2) + 1);

                currReference.child(key.concat(Integer.toString(i))).child("song").setValue(currSong);
                currReference.child(key.concat(Integer.toString(i))).child("artist").setValue(currArtist);
            }
        }
    }

    public static void parseTopSongs(JSONObject jObject) throws JSONException {

        // all tracks are held in an array under the key "items"
        JSONArray jsonSongs = jObject.getJSONArray("items");

        // I made it so that all songs are stored in even indexes, and songs in odd - they alternate
        // so the song name goes first, then the person who made it
        ArrayList<String> songList = new ArrayList<>();

        for (int i = 0; i < jsonSongs.length(); i++) {
            JSONObject currTrack = jsonSongs.getJSONObject(i);
            String currSong = currTrack.getString("name");
            String currArtist = currTrack.getJSONArray("artists").getJSONObject(0).getString("name");

            songList.add(currSong);
            songList.add(currArtist);
        }

        storeList("song", songList);
    }
}