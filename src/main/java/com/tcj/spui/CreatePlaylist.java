package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

public class CreatePlaylist {

  public static void execute() {
    try {
      GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
          .build();

      CreatePlaylistRequest createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(
              getCurrentUsersProfileRequest.execute().getId(), MainHELLO.name)
          .public_(MainHELLO.playlistPublic)
          .build();

      Playlist playlist = createPlaylistRequest.execute();

      System.out.println(ansi().render("@|green Name: |@" + playlist.getName()));
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

}
