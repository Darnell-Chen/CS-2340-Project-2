package com.example.spotifywrapped;

public class Track {
    private String imageURL;
    private String artistName;
    private String trackName;

    Track (String artist, String album, String url) {
        this.imageURL = url;
        this.artistName = artist;
        this.trackName = album;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Track)) {
            return false;
        }

        Track track = (Track) obj;

        if (track.getTrackName().equals(this.trackName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return trackName != null ? trackName.hashCode() : 0;
    }
}
