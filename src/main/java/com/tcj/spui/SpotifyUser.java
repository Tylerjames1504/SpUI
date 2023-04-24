package com.tcj.spui;

import java.util.ArrayList;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public class SpotifyUser {

  final private SpotifyUserAuthorizationManager spotifyUserAuthorizationManager;
  private final ArrayList<TrackSimplified> discoveryPool = new ArrayList<>();
  private final Track[] discoveryShown = new Track[8];
  private Paging<Track> trackPagingShort;
  private Paging<Track> trackPagingLong;
  private Paging<Artist> artistPagingShort;
  private Paging<Artist> artistPagingLong;

  public SpotifyUser() {
    this.spotifyUserAuthorizationManager = new SpotifyUserAuthorizationManager();
  }

  public SpotifyUserAuthorizationManager getUserAuthorizationManager() {
    return this.spotifyUserAuthorizationManager;
  }

  public Paging<Track> getTrackPagingShort() {
    return this.trackPagingShort;
  }

  public void setTrackPagingShort(Paging<Track> trackPagingShort) {
    this.trackPagingShort = trackPagingShort;
  }

  public Paging<Artist> getArtistPagingShort() {
    return this.artistPagingShort;
  }

  public void setArtistPagingShort(Paging<Artist> artistPagingShort) {
    this.artistPagingShort = artistPagingShort;
  }

  public Paging<Track> getTrackPagingLong() {
    return this.trackPagingLong;
  }

  public void setTrackPagingLong(Paging<Track> trackPagingLong) {
    this.trackPagingLong = trackPagingLong;
  }

  public Paging<Artist> getArtistPagingLong() {
    return this.artistPagingLong;
  }

  public void setArtistPagingLong(Paging<Artist> artistPagingLong) {
    this.artistPagingLong = artistPagingLong;
  }

  public ArrayList<TrackSimplified> getDiscoveryPool() { return this.discoveryPool; }

  public Track[] getDiscoveryShown() { return this.discoveryShown; }
}
