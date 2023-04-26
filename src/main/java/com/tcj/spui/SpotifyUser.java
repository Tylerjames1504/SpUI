package com.tcj.spui;

import java.util.ArrayList;
import java.util.List;

import se.michaelthelin.spotify.model_objects.specification.*;

/*
 * Class to wrap the current App's user Spotify data
 * Contains data that was retrieved by the Spotify Web API calls that were made when AppManger created the Scenes
 * This data is used to populate the components on the scenes (Images, Text, etc.)
 * Contains a SpotifyUserAuthorizationManager object which handles the Spotify API auth flow
 */
public class SpotifyUser {

  final private SpotifyUserAuthorizationManager spotifyUserAuthorizationManager;
  private final ArrayList<TrackSimplified> discoveryPool = new ArrayList<>();
  private final Track[] discoveryShown = new Track[8];
  private Paging<Track> trackPagingShort;
  private Paging<Track> trackPagingLong;
  private Paging<Artist> artistPagingShort;
  private Paging<Artist> artistPagingLong;
  private PlaylistBlockCycle playlistHead;
  private List<TrackSimplified> recommendedTracks = new ArrayList();
  private ArrayList<TrackSimplified> newReleases = new ArrayList();

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

  public void setPlaylistHead(PlaylistBlockCycle playlistHead) { this.playlistHead = playlistHead; }
  public PlaylistBlockCycle getPlaylistHead() { return this.playlistHead; }

  public void setRecommendedTracks(List<TrackSimplified> recommendedTracks) { this.recommendedTracks = recommendedTracks; }
  public List<TrackSimplified> getRecommendedTracks() { return this.recommendedTracks; }
  public void setNewReleases(ArrayList<TrackSimplified> newReleases) {this.newReleases = newReleases; }
  public ArrayList<TrackSimplified> getNewReleases() {return this.newReleases;}
}
