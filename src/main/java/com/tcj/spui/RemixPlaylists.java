package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Recommendations;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

public class RemixPlaylists {

  private static final ArrayList<String> playlistArrayList = new ArrayList<String>();


  private static CreatePlaylistRequest createPlaylistRequest;
  private static GetCurrentUsersProfileRequest getCurrentUsersProfileRequest;
  private static AddItemsToPlaylistRequest addItemsToPlaylistRequest;
  private static GetPlaylistsItemsRequest getPlaylistsItemsRequest;
  private static Playlist playlist;
  private static final ArrayList<Playlist> playlists = new ArrayList<Playlist>();
  private static String playlistId;
  private static String[] uris;
  private static int[] randomNumbers;

  static ArrayList<String> urisList = new ArrayList<String>();

  public static void createPlaylist() {
    try {
      getCurrentUsersProfileRequest = MainHELLO.spotifyApi.getCurrentUsersProfile()
          .build();

      createPlaylistRequest = MainHELLO.spotifyApi.createPlaylist(
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

  public static void init() {
    File playlistList = new File("playlistList.txt");
    try {
      Scanner reader = new Scanner(playlistList);
      String data;
      while (reader.hasNextLine()) {
        data = reader.nextLine();
        if (!data.startsWith("-")) {
          playlistArrayList.add(data);
        }
      }
      for (int i = 0; i < playlistArrayList.size(); i++) {
        GetPlaylistRequest getPlaylistRequest = MainHELLO.spotifyApi.getPlaylist(
                playlistArrayList.get(i))
            .build();

        playlists.add(getPlaylistRequest.execute());
      }
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

  public static void addSongs() {
    try {
      for (int i = 0; i < MainHELLO.numPlaylists; i++) {

        getPlaylistsItemsRequest = MainHELLO.spotifyApi
            .getPlaylistsItems(playlists.get(i).getId())
            .build();

        ArrayList<Integer> randomArray = new ArrayList<Integer>();
        for (int z = 0; z < getPlaylistsItemsRequest.execute().getItems().length; z++) {
          randomArray.add(z);
        }
        randomNumbers = new int[MainHELLO.limit];
        Collections.shuffle(randomArray);
        for (int z = 0; z < randomNumbers.length; z++) {
          randomNumbers[z] = randomArray.get(z);
        }

        for (int x = 0; x < MainHELLO.limit; x++) {
          IPlaylistItem playlistItem = getPlaylistsItemsRequest.execute()
              .getItems()[randomNumbers[x]].getTrack();
          if (playlistItem.getType().toString().equals("TRACK")
              && !urisList.contains(playlistItem.getUri())) {
            urisList.add(playlistItem.getUri());
            System.out.println("ADDED: " + playlistItem.getName());
          } else if (!(MainHELLO.genre.length() == 0)) {
            Recommendations rec = GetRecommendations.getRecommendations();

            while (urisList.contains(rec.getTracks()[0].getUri())) {
              rec = GetRecommendations.getRecommendations();
            }
            urisList.add(rec.getTracks()[0].getUri());
            System.out.println("ADDED: " + rec.getTracks()[0].getUri());
          }
        }

        uris = new String[urisList.size()];

        urisList.toArray(uris);
        urisList.clear();
        addItemsToPlaylistRequest = MainHELLO.spotifyApi
            .addItemsToPlaylist(playlistId, uris)
            .build();
        addItemsToPlaylistRequest.execute();
      }
      playlists.clear();
      System.out.println(ansi().render("@|green -----------DONE-----------|@"));
      System.out.println(ansi().render(
          "@|green Spotify url: |@" + playlist.getExternalUrls().getExternalUrls().get("spotify")));

    } catch (Exception e) {
      System.out.println(
          ansi().render("@|red Error: " + e.getMessage() + "\nHint: is playlistList.txt empty?|@"));
    }
  }

  public static int playlistNumbers() {
    File playlistList = new File("playlistList.txt");
    int lineNum = 0;
    try {
      Scanner reader = new Scanner(playlistList);
      String data;
      while (reader.hasNextLine()) {
        data = reader.nextLine();
        if (!data.startsWith("-")) {
          playlistArrayList.add(data);
          lineNum++;
        }
      }
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
    return lineNum;
  }


  public static void execute() {
    System.out.println(ansi().render(
        "@|yellow This might take some time...\nGo grab a coffee or something while im working...|@"));
    init();
    createPlaylist();
    addSongs();
  }
}
