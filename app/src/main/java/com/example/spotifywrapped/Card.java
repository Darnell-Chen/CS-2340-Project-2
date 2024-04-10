package com.example.spotifywrapped;

public class Card{

    private String location;
    private String date;
    private String time;
    private String term;

    // Constructor
    public Card(String pLocation) {
        this.location = pLocation;
        
        parseLocation();
    }

    private void parseLocation() {
        String[] splitLocation = location.split(" ");

        this.date = splitLocation[0];
        this.time = splitLocation[1];
        this.term = splitLocation[2];
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getLocation() {
        System.out.println(location);
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}