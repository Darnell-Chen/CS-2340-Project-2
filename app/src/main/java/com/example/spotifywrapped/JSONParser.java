package com.example.spotifywrapped;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class JSONParser {
    public static void parseTopArtist(JSONObject jObject, AuthViewModel vm) throws JSONException {

        JSONArray jsonArtists = jObject.getJSONArray("items");

        ArrayList<String> artistList = new ArrayList<>();

        for (int i = 0; i < jsonArtists.length(); i++) {
            JSONObject currArtist = jsonArtists.getJSONObject(i);
            artistList.add(currArtist.getString("name"));
            artistList.add(currArtist.getJSONArray("images").getJSONObject(0).getString("url"));
        }
        storeList("artist", artistList, vm);
    }



    private static void storeList(String key, ArrayList<?> value, AuthViewModel vm) {
        DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String newChild = "top ".concat(key).concat("s");

        DatabaseReference currReference = fbDatabase.child("Users").child(auth.getUid().toString()).child(newChild);

        if (key.equals("artist")) {
            for (int i = 0; i < value.size()/2; i++) {
                String currArtist = (String) value.get(i * 2);
                String currImage = (String) value.get((i*2) + 1);

                currReference.child(key.concat(Integer.toString(i))).child("artist").setValue(currArtist);
                currReference.child(key.concat(Integer.toString(i))).child("url").setValue(currImage);
            }

        } else if (key.equals("song")){
            for (int i = 0; i < value.size()/2; i++) {
                ArrayList<Track> songList = (ArrayList<Track>) value;
                String currSong = songList.get(i).getTrackName();
                String currArtist = songList.get((i)).getArtistName();
                String currImage = songList.get((i)).getImageURL();

                currReference.child(key.concat(Integer.toString(i))).child("song").setValue(currSong);
                currReference.child(key.concat(Integer.toString(i))).child("artist").setValue(currArtist);
                currReference.child(key.concat(Integer.toString(i))).child("url").setValue(currImage);
            }

        } else if (key.equals("album")) {
            for (int i = 0; i < value.size(); i++) {
                ArrayList<Track> albumList = (ArrayList<Track>) value;
                String currAlbum = albumList.get(i).getTrackName();
                String currArtist = albumList.get((i)).getArtistName();
                String currImage = albumList.get((i)).getImageURL();
                currReference.child(key.concat(Integer.toString(i))).child("album").setValue(currAlbum);
                currReference.child(key.concat(Integer.toString(i))).child("artist").setValue(currArtist);
                currReference.child(key.concat(Integer.toString(i))).child("url").setValue(currImage);
            }
        } else if (key.equals("genre")) {
            for (int i = 0; i < value.size(); i++) {
                String currGenre = (String) value.get(i);
                currReference.child(key.concat(Integer.toString(i))).setValue(currGenre);
            }
        }

        vm.setRetrieved(vm.getRetrieved().getValue());
    }



    public static void parseTopSongs(JSONObject jObject, AuthViewModel vm) throws JSONException {

        // all tracks are held in an array under the key "items"
        JSONArray jsonSongs = jObject.getJSONArray("items");

        // I made it so that all songs are stored in even indexes, and songs in odd - they alternate
        // so the song name goes first, then the person who made it
        ArrayList<Track> songList = new ArrayList<>();

        for (int i = 0; i < jsonSongs.length(); i++) {
            JSONObject currTrack = jsonSongs.getJSONObject(i);
            String currSong = currTrack.getString("name");
            String currArtist = currTrack.getJSONArray("artists").getJSONObject(0).getString("name");
            String currImage = currTrack.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

            Track newSong = new Track(currArtist, currSong, currImage);
            songList.add(newSong);
        }

        storeList("song", songList, vm);
    }

    public static void parseTopAlbums(JSONObject jObject, AuthViewModel vm) throws JSONException{

        JSONArray jsonTracks = jObject.getJSONArray("items");

        HashMap<Track, Integer> topTrackMap = new HashMap<>();

        for (int i = 0; i < jsonTracks.length(); i++) {
            JSONObject currTrack = jsonTracks.getJSONObject(i).getJSONObject("album");

            String currAlbum = currTrack.getString("name");
            String currImage = currTrack.getJSONArray("images").getJSONObject(0).getString("url");
            String currArtist = currTrack.getJSONArray("artists").getJSONObject(0).getString("name");

            Track newAlbum = new Track(currArtist, currAlbum, currImage);

            topTrackMap.put(newAlbum, topTrackMap.getOrDefault(newAlbum, 0) + 1);
        }

        PriorityQueue<Map.Entry<Track, Integer>> maxHeap = new PriorityQueue<>(
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        maxHeap.addAll(topTrackMap.entrySet());

        int count = 0;

        ArrayList<Track> topAlbumList = new ArrayList<>();

        while (!maxHeap.isEmpty() && count < 10) {
            Map.Entry<Track, Integer> entry = maxHeap.poll();
            topAlbumList.add(entry.getKey());
            count++;
        }

        storeList("album", topAlbumList, vm);
    }

    public static void parseTopGenres(JSONObject jObject, AuthViewModel vm) throws JSONException {
        JSONArray jsonArtists = jObject.getJSONArray("items");

        HashMap<String, Integer> genreMap = new HashMap<>();

        for (int i = 0; i < jsonArtists.length(); i++) {
            JSONArray currGenreList = jsonArtists.getJSONObject(i).getJSONArray("genres");

            for (int j = 0; j < currGenreList.length(); j++) {
                String currGenre = currGenreList.getString(j);
                genreMap.put(currGenre, genreMap.getOrDefault(currGenre, 0) + 1);
            }
        }

        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        maxHeap.addAll(genreMap.entrySet());

        int count = 0;

        ArrayList<String> topGenreList = new ArrayList<>();

        while (!maxHeap.isEmpty() && count < 10) {
            Map.Entry<String, Integer> entry = maxHeap.poll();
            topGenreList.add(entry.getKey());
            count++;
        }

        storeList("genre", topGenreList, vm);
    }
}