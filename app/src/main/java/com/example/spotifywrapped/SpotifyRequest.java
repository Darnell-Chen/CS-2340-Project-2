package com.example.spotifywrapped;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyRequest {

    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    DatabaseReference fbDB = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private Call mCall;
    public void getUserTop(Activity currActivity, String mAccessToken, String requestType, String range) {
        if (mAccessToken == null) {
            Toast.makeText(currActivity, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.getUid();

        // urlRequest is basically requestType, but it chooses the proper input for the Request Builder
        // based on the type the user is requesting (ie. "albums" requires you to pull from "tracks")
        String urlRequest;

        if (requestType.equals("albums")) {
            urlRequest = "tracks";
        } else {
            urlRequest = requestType;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url(makeURL(urlRequest, range))
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(currActivity, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());

                    // parses the JSON response
                    if (requestType.equals("tracks")) {
                        JSONParser.parseTopSongs(jsonObject);
                    } else if (requestType.equals("artists")){
                        JSONParser.parseTopArtist(jsonObject);
                    } else {
                        JSONParser.parseTopAlbums(jsonObject);
                    }

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(currActivity, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public String makeURL(String requestType, String range) {
        String urlRequest;
        if (requestType.equals("album")) {
            urlRequest = "tracks";
        } else {
            urlRequest = requestType;
        }

        String base = "https://api.spotify.com/v1/me/top/".concat(requestType);

        String limit;
        if (requestType.equals("album")) {
            limit = "&limit=100";
        } else {
            limit = "&limit=10";
        }

        if (range != "") {
            base = base.concat("?time_range=").concat(range);
        }

        base = base.concat(limit);

        System.out.println(base);

        return base;
    }
}
