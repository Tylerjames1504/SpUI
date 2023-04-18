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
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

public class CreateExploreArtistsPlaylist {

  private static String[] artistIDs = new String[200];
  private static int offset = 0;
  private static int index = 0;
  private static String q = "genre:";
  private static ArrayList<Integer> setRandom = new ArrayList<>();
  private static int[] randoms = new int[MainHELLO.numArtists];
  private static ArrayList<String> urisArrayList = new ArrayList<>();
  private static Playlist playlist;

  public static void reset() {
    artistIDs = new String[200];
    offset = 0;
    index = 0;
    q = "genre:";
    setRandom = new ArrayList<>();
    randoms = new int[MainHELLO.numArtists];
    urisArrayList = new ArrayList<>();
  }

  public static void execute() {
    reset();
    try {
      q += MainHELLO.genre;
      GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
          .build();

      CreatePlaylistRequest createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(
              getCurrentUsersProfileRequest.execute().getId(), MainHELLO.name)
          .public_(MainHELLO.playlistPublic)
          .build();

      playlist = createPlaylistRequest.execute();
      String playlistId = playlist.getId();

      for (int i = 0; i < 3; i++) {
        SearchArtistsRequest searchArtistsRequest = MainHELLO.spotifyApi.searchArtists(q)
            .market(MainHELLO.location)
            .limit(50)
            .offset(offset)
            .build();

        Paging<Artist> artistPaging = searchArtistsRequest.execute();
        for (int x = 0; x < 50; x++) {
          artistIDs[index] = artistPaging.getItems()[x].getId();
          index++;
        }
        offset += 50;
      }

      for (int x = 0; x < 199; x++) {
        setRandom.add(x);
      }
      Collections.shuffle(setRandom);
      for (int i = 0; i < MainHELLO.numArtists; i++) {
        randoms[i] = setRandom.get(i);
      }

      for (int i = 0; i < MainHELLO.numArtists; i++) {
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = MainHELLO.spotifyApi.getArtistsAlbums(
                artistIDs[i])
            .album_type("album")
            .limit(50)
            .market(MainHELLO.location)
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
          AddItemsToPlaylistRequest addItemsToPlaylistRequest = MainHELLO.spotifyApi
              .addItemsToPlaylist(playlistId, uris)
              .build();
          SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();

        }
      }
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
    System.out.println(ansi().render("@|green -----------DONE-----------|@"));
    System.out.println(ansi().render(
        "@|green Spotify url: |@" + playlist.getExternalUrls().getExternalUrls().get("spotify")));
  }
}