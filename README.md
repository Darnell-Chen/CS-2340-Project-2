# Spotify UnWrapped
Welcome to Spotify Unwrapped!

We're a clone of Spotify Wrapped, except we are able to be used year-round. 
Our App enables users to create summaries of listening habits in three different ranges:

- Long Term (Approx. Past Year)
- Medium Term (Approx. Past 6 Months)
- Short Term (Approx. Past Month)

All Wrapped can be saved and stored to Firebase Servers to be viewed in the future.

Made by students at Georgia Institute of Technology.


## Demo
Here's a small snippet of what our app is about
(Credit to Tarun Ramji):

[![Spotify UnWrapped](https://img.youtube.com/vi/_Fh7P1hxAhg/0.jpg)](https://www.youtube.com/watch?v=_Fh7P1hxAhg)


## How to Use the App
To use the program, please include the following three into your local.properties file:


```Java
OPENAI_API_KEY = your_api_key

CLIENT_ID = your_spotify_client_id

REDIRECT_URI = spotify_redirect_uri
```


Here's an example:

```Java
OPENAI_API_KEY = sk-abcdefhijklmnopqrstuwxxxxxxxxxx

CLIENT_ID = 123abcxxxxxxxxxx

REDIRECT_URI = mySpotifyWrapped://auth
```

Documentation concerning Spotify API can be found [here](https://developer.spotify.com/documentation/web-api).
Steps to getting your client id and redirect uri can also be found in the link above.

You can then follow the demo to create an account!

Note: Due to limitations imposed by the Spotify API, our Wrapped may not be 100% accurate.
