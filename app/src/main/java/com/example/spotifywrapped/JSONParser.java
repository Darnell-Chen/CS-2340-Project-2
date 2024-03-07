package com.example.spotifywrapped;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        for (int i = 0; i < value.size(); i++) {
            currReference.child(key.concat(Integer.toString(i))).setValue(value.get(i));
        }
    }

    public static void parseTopSongs(JSONObject jObject) throws JSONException {

        JSONArray jsonSongs = jObject.getJSONArray("items");

        ArrayList<String> songsList = new ArrayList<>();

        for (int i = 0; i < jsonSongs.length(); i++) {
            JSONObject currArtist = jsonSongs.getJSONObject(i);
            songsList.add(currArtist.getString("name"));
        }

        storeList("song", songsList);
    }
}