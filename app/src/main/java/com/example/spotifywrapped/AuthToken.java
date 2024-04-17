package com.example.spotifywrapped;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class AuthToken {
    public static String CLIENT_ID;
    public static String REDIRECT_URI;
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;

    public void getToken(Activity currActivity) {
        CLIENT_ID = BuildConfig.CLIENT_ID;
        REDIRECT_URI = BuildConfig.REDIRECT_URI;
        if (CLIENT_ID == null || REDIRECT_URI == null) {
            Toast.makeText(currActivity, "Please Make Sure to input Client ID and Redirect URI in local properties", Toast.LENGTH_SHORT).show();
        } else {
            final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
            AuthorizationClient.openLoginActivity(currActivity, AUTH_TOKEN_REQUEST_CODE, request);
        }
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }
}
