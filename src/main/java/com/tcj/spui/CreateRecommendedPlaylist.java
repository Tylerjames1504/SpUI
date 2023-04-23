package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;


public class CreateRecommendedPlaylist {

  private static Playlist playlist;
  private static String playlistId;
  private static final String[] uris = new String[50];

  public static void createPlaylist() {
    try {
      GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
          .build();

      CreatePlaylistRequest createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(
              getCurrentUsersProfileRequest.execute().getId(), MainHELLO.name)
          .public_(MainHELLO.playlistPublic)
          .build();

      playlist = createPlaylistRequest.execute();
      playlistId = playlist.getId();

    } catch (Exception e) {
      System.out.println(ansi().render(
          "@|red Error: " + e.getMessage() + "\nTIP: make sure your genre exists!|@"));
    }
  }

  public static void addSongs() {
    Recommendations recommendations = GetRecommendations.getFullRecommendations();
    for (int i = 0; i < 50; i++) {
      uris[i] = recommendations.getTracks()[i].getUri();
    }
    AddItemsToPlaylistRequest addItemsToPlaylistRequest = MainHELLO.spotifyApi
        .addItemsToPlaylist(playlistId, uris)
//          .position(0)
        .build();
    try {
      addItemsToPlaylistRequest.execute();
      System.out.println(ansi().render("@|green -----------DONE-----------|@"));
      System.out.println(ansi().render(
          "@|green Spotify url: |@" + playlist.getExternalUrls().getExternalUrls().get("spotify")));
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }

  }

  public static void execute() {
    System.out.println(ansi().render(
        "@|yellow This might take some time...\nGo grab a coffee or something while im working...|@"));
    createPlaylist();
    addSongs();
  }


}
