package com.tcj.spui;

import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.ArrayList;

public class SpotifyUser {
    final private SpotifyUserAuthorizationManager spotifyUserAuthorizationManager;
    private Paging<Track> trackPagingShort;
    private Paging<Track> trackPagingLong;
    private Paging<Artist> artistPagingShort;
    private Paging<Artist> artistPagingLong;

    private final ArrayList<TrackSimplified> discoveryPool = new ArrayList<>();
    private final Track[] discoveryShown = new Track[8];

    public SpotifyUser() {
        this.spotifyUserAuthorizationManager = new SpotifyUserAuthorizationManager();
    }
    public SpotifyUserAuthorizationManager getUserAuthorizationManager() { return this.spotifyUserAuthorizationManager; }

    public void setTrackPagingShort(Paging<Track> trackPagingShort) {this.trackPagingShort = trackPagingShort; }
    public void setArtistPagingShort(Paging<Artist> artistPagingShort) {this.artistPagingShort = artistPagingShort; }
    public void setTrackPagingLong(Paging<Track> trackPagingLong) {this.trackPagingLong = trackPagingLong; }
    public void setArtistPagingLong(Paging<Artist> artistPagingLong) {this.artistPagingLong = artistPagingLong; }
    public Paging<Track> getTrackPagingShort() { return this.trackPagingShort; }
    public Paging<Artist> getArtistPagingShort() { return this.artistPagingShort; }
    public Paging<Track> getTrackPagingLong() { return this.trackPagingLong; }
    public Paging<Artist> getArtistPagingLong() { return this.artistPagingLong; }
    public ArrayList<TrackSimplified> getDiscoveryPool() { return this.discoveryPool; }
    public Track[] getDiscoveryShown() { return this.discoveryShown; }
}