//package com.tcj.spui;
//
//import static org.fusesource.jansi.Ansi.ansi;
//
//import java.util.Scanner;
//
//public class SearchGenre {
//
//  private static String alphabet = "";
//
//  public static void execute() {
//    Scanner input = new Scanner(System.in);
//    System.out.println(
//        ansi().render("@|green Please enter the starting alphabet of the genre to search|@"));
//    alphabet = input.nextLine();
//    while (alphabet.length() > 1) {
//      System.out.println(ansi().render(
//          "@|green Please enter the starting alphabet only!\nPlease enter the alphabet again:|@"));
//      alphabet = input.nextLine();
//    }
//    try {
//      final String[] strings = MainHELLO.spotifyApi.getAvailableGenreSeeds().build().execute();
//      char[] genreChar;
//      char[] alphabetChar = alphabet.toCharArray();
//      System.out.println();
//      for (String genre : strings) {
//        genreChar = genre.toCharArray();
//        if (genreChar[0] == alphabetChar[0]) {
//          System.out.println(genre);
//        }
//      }
//    } catch (Exception e) {
//      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
//    }
//  }
//}
