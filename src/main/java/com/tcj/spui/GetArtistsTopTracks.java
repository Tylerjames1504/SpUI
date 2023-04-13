package com.tcj.spui;

import static org.fusesource.jansi.Ansi.ansi;

import com.neovisionaries.i18n.CountryCode;
import java.util.Scanner;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;

public class GetArtistsTopTracks {

  private final SpotifyApi spotifyApi;
  private int index;
  private final Scanner input;
  private static GetArtistsTopTracksRequest getArtistsTopTracksRequest;
  private final CountryCode countryCode;
  private final GetSearchArtists searchArtists;

  GetArtistsTopTracks(SpotifyApi spotifyApi, String name, CountryCode countryCode) {
    this.spotifyApi = spotifyApi;
    this.countryCode = countryCode;
    this.input = new Scanner(System.in);

    searchArtists = new GetSearchArtists(spotifyApi, name);

  }

  public void setArtistPaging() {
    getArtistsTopTracksRequest = spotifyApi
        .getArtistsTopTracks(searchArtists.getArtist().getItems()[index].getId(), countryCode)
        .build();
  }


  public void inputIndex() {
    System.out.println(searchArtists);
    System.out.print(ansi().render("@|green Input the number of your artist: |@"));
    index = input.nextInt();
    System.out.println();
    while (index > 4 || index < 0) {
      System.out.println(ansi().render("@|red Error: wrong index|@"));
      System.out.print(ansi().render("@|green Please enter the index again: |@"));
      index = input.nextInt();
    }


  }

  public static void getArtistsTopTracks_Sync() {
    try {
      Track[] tracks = getArtistsTopTracksRequest.execute();
      for (int i = 0; i < tracks.length; i++) {
        System.out.print(ansi().render("@|green " + (i + 1) + ": |@") + tracks[i].getName() + " ");
        System.out.printf(ansi().render("@|green (%d:%02d) |@").toString(),
            ((tracks[i].getDurationMs() / 1000) / 60), (tracks[i].getDurationMs() / 1000) % 60);
        System.out.println(
            ansi().render("@|yellow (popularity: " + tracks[i].getPopularity() + ")|@"));
      }
    } catch (Exception e) {
      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
    }
  }

  public void execute() {
    inputIndex();
    setArtistPaging();

    getArtistsTopTracks_Sync();
  }


}
