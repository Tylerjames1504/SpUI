//package com.tcj.spui;
//
//import static org.fusesource.jansi.Ansi.ansi;
//
//import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;
//
//public class GetSeedGenreList {
//
//  private static final GetAvailableGenreSeedsRequest getAvailableGenreSeedsRequest = MainHELLO.spotifyApi.getAvailableGenreSeeds()
//      .build();
//
//  public static void getAvailableGenreSeeds_Sync() {
//    try {
//      final String[] strings = getAvailableGenreSeedsRequest.execute();
//
//      for (String string : strings) {
//        System.out.println(string);
//      }
//    } catch (Exception e) {
//      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
//    }
//  }
//
//  public static void execute() {
//    getAvailableGenreSeeds_Sync();
//  }
//}
