package com.tcj.spui;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

public class HomePageController extends SceneUtilities {

  @FXML
  Label errorLabelSongs;
  @FXML
  Label errorLabelArtists;
  @FXML
  ToggleButton changeSongs;
  @FXML
  ToggleButton changeArtists;

  private int songButtonState = 1;
  private int artistButtonState = 1;

  private int silentRefreshCounter = 0;

  public void initialize() {
    this.parentStageKey = "main";
    this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey)
        .getParentStage();
    loadSongs("short_term");
    loadSongs("long_term");
    loadArtists("short_term");
    loadArtists("long_term");
    topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
      if (newScene != null) {
        setupWindowBarDrag();
        this.currentScene = topBar.getScene();
        displaySongs();
        displayArtists();
        displayDiscovery();
      }
    });
  }
  public void swapScene(){
    swapScene("playlistScene", "Playlist Manager");
  }

  public void loadDiscovery() {
    StringBuilder artistsBuilder = new StringBuilder();
    StringBuilder tracksBuilder = new StringBuilder();
    for (int i = 0; i < App.session.getAppUser().getArtistPagingShort().getItems().length; i++) {
      if (i == 3) {
        break;
      }
      artistsBuilder.append(App.session.getAppUser().getArtistPagingShort().getItems()[i].getId());
      if (i < 2 && i != App.session.getAppUser().getArtistPagingShort().getItems().length) {
        artistsBuilder.append(",");
      }
    }
    for (int i = 0; i < App.session.getAppUser().getTrackPagingShort().getItems().length; i++) {
      if (i == 2) {
        break;
      }
      tracksBuilder.append(App.session.getAppUser().getTrackPagingShort().getItems()[i].getId());
      if (i < 1 && i != App.session.getAppUser().getTrackPagingShort().getItems().length) {
        tracksBuilder.append(",");
      }
    }
    final GetRecommendationsRequest getRecommendationsRequest = this.spotifyApi.getRecommendations()
            .limit(1)
            .seed_artists(artistsBuilder.toString())
            .seed_tracks(tracksBuilder.toString())
            .build();
    final GetListOfNewReleasesRequest getListOfNewReleasesRequest = this.spotifyApi.getListOfNewReleases()
            .limit(1)
            .build();
    try {
      List<TrackSimplified> recommendedTracks = new ArrayList<>();
      if (App.session.getAppUser().getArtistPagingShort().getItems().length > 0) {
        Recommendations recommendations = getRecommendationsRequest.execute();
        recommendedTracks = Arrays.asList(recommendations.getTracks());
      }
      System.out.println("here");
      Paging<AlbumSimplified> newReleases = getListOfNewReleasesRequest.execute();
      for (int i = 0; i < newReleases.getItems().length; i++) {
        GetAlbumsTracksRequest getAlbumsTracksRequest = this.spotifyApi.getAlbumsTracks(newReleases.getItems()[i].getId()).build();
        Paging<TrackSimplified> albumsTracks = getAlbumsTracksRequest.execute();
        App.session.getAppUser().getDiscoveryPool().add(albumsTracks.getItems()[0]);
      }
      App.session.getAppUser().getDiscoveryPool().addAll(recommendedTracks);
    } catch (IOException | ParseException | SpotifyWebApiException e) {
      throw new RuntimeException(e);
    }
    System.out.println("here");
  }

  public void displayDiscovery() {
    if (App.session.getAppUser().getDiscoveryPool().size() < 8) {
      App.session.getAppUser().getDiscoveryPool().clear();
      loadDiscovery();
    }
    App.session.getAppUser().getUserAuthorizationManager().refreshAuthCode();
    try {
      int bound = App.session.getAppUser().getDiscoveryPool().size();
      int size = bound;
      Random random = new Random();
      for (int i = 0; i < size; i++) {
        if (i == 8) {
          break;
        }
        int index = random.nextInt(0, bound);
        Track track = this.spotifyApi.getTrack(App.session.getAppUser().getDiscoveryPool().get(index).getId()).build().execute();
        App.session.getAppUser().getDiscoveryShown()[i] = track;
        Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 57, 57, false, false);
        ((ImageView) currentScene.lookup("#discovImage" + i)).setImage(trackImage);
        String trackName = track.getName();
        if (trackName.length() > 20) {
          trackName = trackName.substring(0, 19) + "...";
        }
        ArtistSimplified[] artists = track.getArtists();
        StringBuilder allArtists = new StringBuilder("- ");
        for (int k = 0; k < artists.length; k++) {
          allArtists.append(artists[k].getName());
          if (k != artists.length - 1) {
            allArtists.append(", ");
          }
        }
        if (allArtists.length() > 20) {
          allArtists = new StringBuilder(allArtists.substring(0, 19) + "...");
        }
        ((Label) currentScene.lookup("#discovInfo" + i)).setText("  " + trackName + "\n  " + allArtists);
        App.session.getAppUser().getDiscoveryPool().remove(index);
        bound--;
      }
    } catch (IOException | ParseException | SpotifyWebApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadSongs(String term) {
    final GetUsersTopTracksRequest getUsersTopTracksRequest = this.spotifyApi.getUsersTopTracks()
        .limit(10)
        .offset(0)
        .time_range(term)
        .build();
    SpotifyUser tempSpotifyUser = App.session.getAppUser();
    try {
      if (term.equals("short_term")) {
        tempSpotifyUser.setTrackPagingShort(getUsersTopTracksRequest.execute());
      }
      if (term.equals("long_term")) {
        tempSpotifyUser.setTrackPagingLong(getUsersTopTracksRequest.execute());
      }
    } catch (IOException | ParseException | SpotifyWebApiException e) {
      throw new RuntimeException(e);
    }
  }
  public void changeButtonState(MouseEvent event) {
    silentRefreshCounter++;
    if (silentRefreshCounter == 8) {
      loadSongs("short_term");
      loadSongs("long_term");
      loadArtists("short_term");
      loadArtists("long_term");
      silentRefreshCounter = 0;
    }
    String[] source = parseSource(event.getSource());
    if (source[1].toLowerCase().contains("song")) {
      if (songButtonState == 1)  {changeSongs.setText("Show Recent"); }
      else changeSongs.setText("Show All Time");
      songButtonState *= -1;
      displaySongs();
    }
    if (source[1].toLowerCase().contains("artist")) {
      if (artistButtonState == 1)  {changeArtists.setText("Show Recent"); }
      else changeArtists.setText("Show All Time");
      artistButtonState *= -1;
      displayArtists();
    }

  }

  public void displaySongs() {
    SpotifyUser tempSpotifyUser = App.session.getAppUser();
    tempSpotifyUser.getUserAuthorizationManager().refreshAuthCode();
    Paging<Track> trackPaging;
    if (songButtonState == -1) {
      trackPaging = tempSpotifyUser.getTrackPagingLong();
    } else {
      trackPaging = tempSpotifyUser.getTrackPagingShort();
    }
    int size = trackPaging.getItems().length;
    if (size != 0) {
      errorLabelSongs.setText("");
      changeSongs.setDisable(false);
    }
    for (int i = 0; i < size; i++) {
      Node node = currentScene.lookup("#songBackground" + i);
      node.setDisable(false);
      node.setVisible(true);
      Track track = trackPaging.getItems()[i];
      Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 50, 50, false, false);
      ((ImageView) currentScene.lookup("#topSongImage" + i)).setImage(trackImage);
      String trackName = track.getName();
      if (trackName.length() > 20) {
        trackName = trackName.substring(0, 19) + "...";
      }
      ArtistSimplified[] artists = track.getArtists();
      StringBuilder allArtists = new StringBuilder("- ");
      for (int k = 0; k < artists.length; k++) {
        allArtists.append(artists[k].getName());
          if (k != artists.length - 1) {
              allArtists.append(", ");
          }
      }
      if (allArtists.length() > 20) {
        allArtists = new StringBuilder(allArtists.substring(0, 19) + "...");
      }
      ((Label) currentScene.lookup("#topSongInfo" + i)).setText("  " + trackName + "\n  " + allArtists);
      ((Label) currentScene.lookup("#topSongPopu" + i)).setText(popToString(track.getPopularity()));
    }
    for (int i = size; i < 10; i++) {
      Node node = currentScene.lookup("#songBackground" + i);
      node.setDisable(true);
      node.setVisible(false);
    }
    if (size == 0) {
      errorLabelSongs.setText("Oops.. looks like you have no top songs");
    }
  }

  public void loadArtists(String term) {
    final GetUsersTopArtistsRequest getUsersTopArtistsRequest = this.spotifyApi.getUsersTopArtists()
        .limit(10)
        .offset(0)
        .time_range(term)
        .build();
    SpotifyUser tempSpotifyUser = App.session.getAppUser();
    try {
      if (term.equals("short_term")) {
        tempSpotifyUser.setArtistPagingShort(getUsersTopArtistsRequest.execute());
      }
      if (term.equals("long_term")) {
        tempSpotifyUser.setArtistPagingLong(getUsersTopArtistsRequest.execute());
      }
    } catch (IOException | ParseException | SpotifyWebApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void displayArtists() {
    SpotifyUser tempSpotifyUser = App.session.getAppUser();
    tempSpotifyUser.getUserAuthorizationManager().refreshAuthCode();
    Paging<Artist> artistPaging;
    if (artistButtonState == -1) {
      artistPaging = tempSpotifyUser.getArtistPagingLong();
    } else {
      artistPaging = tempSpotifyUser.getArtistPagingShort();
    }
    int size = artistPaging.getItems().length;
    if (size != 0) {
      errorLabelArtists.setText("");
      changeArtists.setDisable(false);
    }
    for (int i = 0; i < size; i++) {
      Node node = currentScene.lookup("#artistBackground" + i);
      node.setDisable(false);
      node.setVisible(true);
      Artist artist = artistPaging.getItems()[i];
      Image artistImage = new Image(artist.getImages()[0].getUrl(), 50, 50, false, false);
      ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
      String artistName = artist.getName();
      if (artistName.length() > 20) {
        artistName = artistName.substring(0, 19) + "...";
      }
      ((Label) currentScene.lookup("#topArtistInfo" + i)).setText("  " + artistName);
      ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
    }
    for (int i = size; i < 10; i++) {
      Node node = currentScene.lookup("#artistBackground" + i);
      node.setDisable(true);
      node.setVisible(false);
    }
    if (size == 0) {
      errorLabelArtists.setText("Oops.. looks like you have no top artists");
    }
  }

  public void onClickExpandable(MouseEvent event) {
    App.session.getAppUser().getUserAuthorizationManager().refreshAuthCode();
    String[] sourceInfo = parseSource(event.getSource());
    int index = Integer.parseInt(sourceInfo[1].substring(sourceInfo[1].length() - 1));
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("linux")) {
      try {
        if (sourceInfo[1].toLowerCase().contains("song")) {
          if (songButtonState == 1) {
            Runtime.getRuntime()
                    .exec("xdg-open " + App.session.getAppUser().getTrackPagingShort().getItems()[index].getExternalUrls().get("spotify"));
          } else {
            Runtime.getRuntime()
                    .exec("xdg-open " + App.session.getAppUser().getTrackPagingLong().getItems()[index].getExternalUrls().get("spotify"));
          }
        }
        if (sourceInfo[1].toLowerCase().contains("artist")) {
          if (artistButtonState == 1) {
            Runtime.getRuntime()
                    .exec("xdg-open " + App.session.getAppUser().getArtistPagingShort().getItems()[index].getExternalUrls().get("spotify"));
          } else {
            Runtime.getRuntime()
                    .exec("xdg-open " + App.session.getAppUser().getArtistPagingLong().getItems()[index].getExternalUrls().get("spotify"));
          }
        }
        if (sourceInfo[1].toLowerCase().contains("discov")) {
          Runtime.getRuntime()
                  .exec("xdg-open " + App.session.getAppUser().getDiscoveryShown()[index].getExternalUrls().get("spotify"));
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      try {
        App.session.getAppUser().getUserAuthorizationManager().refreshAuthCode();
        if (sourceInfo[1].toLowerCase().contains("song")) {
          if (songButtonState == 1) {
            Desktop.getDesktop()
                    .browse(new URI(App.session.getAppUser().getTrackPagingShort().getItems()[index].getExternalUrls().get("spotify")));
          } else {
            Desktop.getDesktop()
                    .browse(new URI(App.session.getAppUser().getTrackPagingLong().getItems()[index].getExternalUrls().get("spotify")));
          }
        }
        if (sourceInfo[1].toLowerCase().contains("artist")) {
          if (artistButtonState == 1) {
            Desktop.getDesktop()
                    .browse(new URI(App.session.getAppUser().getArtistPagingShort().getItems()[index].getExternalUrls().get("spotify")));
          } else {
            Desktop.getDesktop()
                    .browse(new URI(App.session.getAppUser().getArtistPagingLong().getItems()[index].getExternalUrls().get("spotify")));
          }
        }
        if (sourceInfo[1].toLowerCase().contains("discov")) {
          Desktop.getDesktop()
                  .browse(new URI(App.session.getAppUser().getDiscoveryShown()[index].getExternalUrls().get("spotify")));
        }
      } catch (IOException | URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public String popToString(int popularity) {
      if (popularity >= 90) {
          return "Very Popular";
      } else if (popularity >= 67) {
          return "Popular";
      } else if (popularity >= 45) {
          return "Less Popular";
      } else {
          return "Not That Popular";
      }
  }

}

