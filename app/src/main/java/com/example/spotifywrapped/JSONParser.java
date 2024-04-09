package com.example.spotifywrapped;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class JSONParser {
    public static void parseTopArtist(JSONObject jObject, AuthViewModel vm, String range) throws JSONException {

        JSONArray jsonArtists = jObject.getJSONArray("items");

        ArrayList<String> artistList = new ArrayList<>();

        for (int i = 0; i < jsonArtists.length(); i++) {
            JSONObject currArtist = jsonArtists.getJSONObject(i);
            artistList.add(currArtist.getString("name"));
            artistList.add(currArtist.getJSONArray("images").getJSONObject(0).getString("url"));
        }
        storeList("artist", artistList, vm, range);
    }



    private static void storeList(String key, ArrayList<?> value, AuthViewModel vm, String range) {
        DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String newChild = "top ".concat(key).concat("s");

        DatabaseReference currReference = fbDatabase.child("Users").child(auth.getUid().toString()).child(range).child(newChild);

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
                String currImage = songList.get((i)).getURL();

                currReference.child(key.concat(Integer.toString(i))).child("song").setValue(currSong);
                currReference.child(key.concat(Integer.toString(i))).child("artist").setValue(currArtist);
                currReference.child(key.concat(Integer.toString(i))).child("url").setValue(currImage);
            }

        } else if (key.equals("album")) {
            for (int i = 0; i < value.size(); i++) {
                ArrayList<Track> albumList = (ArrayList<Track>) value;
                String currAlbum = albumList.get(i).getTrackName();
                String currArtist = albumList.get((i)).getArtistName();
                String currImage = albumList.get((i)).getURL();
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
        // TODO: Check
        else if (key.equals("audio")) {
            ArrayList<Track> trackList = (ArrayList<Track>) value;

            for (int i = 0; i < trackList.size(); i++) {
                DatabaseReference childReference = currReference.child(key.concat(Integer.toString(i)));

                String audioURL = (String) trackList.get(i).getURL();
                String artistName = (String) trackList.get(i).getArtistName();
                String songName = (String) trackList.get(i).getTrackName();

                childReference.child("url").setValue(audioURL);
                childReference.child("artist").setValue(artistName);
                childReference.child("song").setValue(songName);
            }
        }

        // we call audio inside of getTopAlbums to prevent calling the same api twice, so we don't increase count for audio
        if (!key.equals("audio") && (vm.getRangeRetrieved().getValue() < vm.getMax_range())) {
            if (vm.getRangeRetrieved().getValue() == vm.getMax_range() - 1) {
                // setRequestRetrieved() automatically adds by one
                vm.setRequestRetrieved(vm.getRequestRetrieved());

                // might want to have this second in case the program decides to race async threads
                vm.postRangeRetrieved(0);
            } else {
                vm.postRangeRetrieved(vm.getRangeRetrieved().getValue() + 1);
            }
        }
    }



    public static void parseTopSongs(JSONObject jObject, AuthViewModel vm, String range) throws JSONException {

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

        storeList("song", songList, vm, range);
    }

    public static void parseTopAlbums(JSONObject jObject, AuthViewModel vm, String range) throws JSONException{

        parseAudio(jObject, vm, range);

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

        while (!maxHeap.isEmpty() && count < 5) {
            Map.Entry<Track, Integer> entry = maxHeap.poll();
            topAlbumList.add(entry.getKey());
            count++;
        }

        storeList("album", topAlbumList, vm, range);
    }

    private static void parseAudio(JSONObject jObject, AuthViewModel vm, String range) throws JSONException {
        JSONArray jsonTracks = jObject.getJSONArray("items");

        ArrayList<Track> trackList = new ArrayList<>();

        for (int i = 0; i < jsonTracks.length() && i < 20; i++) {
            JSONObject currTrackItem = jsonTracks.getJSONObject(i);
            String songName = currTrackItem.getString("name");
            String artistName = currTrackItem.getJSONArray("artists").getJSONObject(0).getString("name");
            String previewURL = currTrackItem.getString("preview_url");
            Track currTrack = new Track(artistName, songName, previewURL);
            trackList.add(currTrack);
        }
        storeList("audio", trackList, vm, range);
    }

    public static void parseTopGenres(JSONObject jObject, AuthViewModel vm, String range) throws JSONException {
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

        while (!maxHeap.isEmpty() && count < 5) {
            Map.Entry<String, Integer> entry = maxHeap.poll();
            topGenreList.add(entry.getKey());
            count++;
        }

        storeList("genre", topGenreList, vm, range);
    }

    public static void parseUserProfile(JSONObject jsonObject, AuthViewModel vm) throws JSONException {
        //get display name
        String userName = jsonObject.getString("display_name");
        //get image

        JSONArray userImageRef = jsonObject.getJSONArray("images");

        String userImage = "https://i1.sndcdn.com/artworks-kzdRRliuzKhhsTvk-Vx9EJg-t500x500.jpg";

        int imgArraySize = userImageRef.length();

        if (imgArraySize > 0) {
            userImage = userImageRef.getJSONObject(imgArraySize - 1).getString("url");
        }

        System.out.println(imgArraySize);

        System.out.println("username: " + userName);

        //Store it to firebase
        DatabaseReference fbDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        DatabaseReference currReference = fbDatabase.child("Users").child(auth.getUid().toString()).child("profile");
        currReference.child("name").setValue(userName);
        currReference.child("image").setValue(userImage);

        vm.postRangeRetrieved(vm.getRangeRetrieved().getValue() + 1);
    }
}