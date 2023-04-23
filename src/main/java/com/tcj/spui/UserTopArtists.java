package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

public class UserTopArtists {

  private static GetUsersTopArtistsRequest getUsersTopArtistsRequest;
  private static int offset;
  private static ArrayList<String> randomArtistId;
  private static ArrayList<String> urisArrayList;


  public static void getUsersTopArtists() {
    try {

      getUsersTopArtistsRequest = MainHELLO.spotifyApi.getUsersTopArtists()
          .limit(MainHELLO.limit)
          .build();

      final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
      int i = 1;
      for (Artist artist : artistPaging.getItems()) {
        System.out.println(i + ": " + artist.getName());
        i++;
      }


    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

  public static void createUsersTopArtistsPlaylist() {

    try {
      randomArtistId = new ArrayList<String>();
      urisArrayList = new ArrayList<String>();
      offset = 0;

      GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
          .build();

      CreatePlaylistRequest createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(
              getCurrentUsersProfileRequest.execute().getId(), MainHELLO.name)
          .public_(MainHELLO.playlistPublic)
          .build();

      Playlist playlist = createPlaylistRequest.execute();
      String playlistId = playlist.getId();

      final Paging<Artist> artistPaging = MainHELLO.spotifyApi.getUsersTopArtists()
          .limit(50)
          .build()
          .execute();
      for (int i = 0; i < MainHELLO.numArtists; i++) {
        randomArtistId.add(artistPaging.getItems()[i].getId());
      }
      Collections.shuffle(randomArtistId);
      for (int i = 0; i < MainHELLO.numArtists; i++) {
        String artistId = randomArtistId.get(i);

        GetArtistsAlbumsRequest getArtistsAlbumsRequest = MainHELLO.spotifyApi.getArtistsAlbums(
                artistId)
            .album_type("album")
            .limit(50)
            .build();
        final Paging<AlbumSimplified> albumSimplifiedPaging = getArtistsAlbumsRequest.execute();
        if (albumSimplifiedPaging.getItems().length > 1) {
          for (int x = 0; x < MainHELLO.limit; x++) {
            int randomNum = ThreadLocalRandom.current()
                .nextInt(0, albumSimplifiedPaging.getItems().length);

            GetAlbumsTracksRequest getAlbumsTracksRequest = MainHELLO.spotifyApi.getAlbumsTracks(
                    albumSimplifiedPaging.getItems()[randomNum].getId())
                .limit(50)
                .build();

            Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();

            int trackRandomNum = ThreadLocalRandom.current()
                .nextInt(0, trackSimplifiedPaging.getItems().length);
            if (!urisArrayList.contains(
                trackSimplifiedPaging.getItems()[trackRandomNum].getUri())) {
              if (!MainHELLO.includeInstrumental
                  && !trackSimplifiedPaging.getItems()[trackRandomNum].getName().toLowerCase()
                  .contains("instrumental")) {
                urisArrayList.add(trackSimplifiedPaging.getItems()[trackRandomNum].getUri());
                System.out.println(
                    "ADDED: " + trackSimplifiedPaging.getItems()[trackRandomNum].getName());
                Thread.sleep(200);
              }
              if (MainHELLO.includeInstrumental) {
                urisArrayList.add(trackSimplifiedPaging.getItems()[trackRandomNum].getUri());
                System.out.println(
                    "ADDED: " + trackSimplifiedPaging.getItems()[trackRandomNum].getName());
                Thread.sleep(200);
              }
            }
          }

          String[] uris = new String[urisArrayList.size()];
          urisArrayList.toArray(uris);
          urisArrayList.clear();
          SnapshotResult addItemsToPlaylistRequest = MainHELLO.spotifyApi
              .addItemsToPlaylist(playlistId, uris)
              .build()
              .execute();
        }
      }

      System.out.println(ansi().render("@|green -----------DONE-----------|@"));
      System.out.println(ansi().render(
          "@|green Spotify url: |@" + playlist.getExternalUrls().getExternalUrls().get("spotify")));


    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }
}
