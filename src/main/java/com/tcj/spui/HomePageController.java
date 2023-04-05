package com.tcj.spui;
import se.michaelthelin.spotify.SpotifyApi;

public class HomePageController {
    private SpotifyApi spotifyApi;
    public HomePageController() {
        this.spotifyApi = App.session.grabApi();
    }
}
