//package com.tcj.spui;
//
//import static org.fusesource.jansi.Ansi.ansi;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import se.michaelthelin.spotify.model_objects.specification.Category;
//import se.michaelthelin.spotify.model_objects.specification.Paging;
//import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
//import se.michaelthelin.spotify.requests.data.browse.GetCategorysPlaylistsRequest;
//import se.michaelthelin.spotify.requests.data.browse.GetListOfCategoriesRequest;
//
//public class GetCategoryPlaylist {
//
//  public static int offset;
//
//
//  public static Paging<PlaylistSimplified> getPlaylists() {
//    try {
//      //          .offset(0)
//      GetCategorysPlaylistsRequest getCategoryRequest = MainHELLO.spotifyApi.getCategorysPlaylists(
//              MainHELLO.category)
//          .country(MainHELLO.location)
//          .limit(50)
////          .offset(0)
//          .build();
//
//      return getCategoryRequest.execute();
//    } catch (Exception e) {
//      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
//      return null;
//    }
//  }
//
//  public static void printCategoryList() {
//    try {
//      offset = 0;
//      boolean isListComplete = false;
//      ArrayList<String> categories = new ArrayList<>();
//      while (!isListComplete) {
//        GetListOfCategoriesRequest getListOfCategoriesRequest = MainHELLO.spotifyApi.getListOfCategories()
//            .limit(50)
//            .offset(offset)
//            .build();
//        Paging<Category> categoryPaging = getListOfCategoriesRequest.execute();
//        for (int i = 0; i < categoryPaging.getItems().length; i++) {
//          categories.add(categoryPaging.getItems()[i].getName());
//        }
//        offset += 50;
//        if (categoryPaging.getItems().length == 0) {
//          isListComplete = true;
//        }
//      }
//
//      Collections.sort(categories);
//      for (String s : categories) {
//        System.out.println(s);
//      }
//    } catch (Exception e) {
//      System.out.println(ansi().render("@|red Error: " + e.getMessage() + "|@"));
//    }
//  }
//
//}
