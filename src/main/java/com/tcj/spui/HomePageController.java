package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;

public class HomePageController extends SceneUtilities {
    @FXML
    Label errorLabelSongs;
    @FXML
    Label errorLabelArtists;
    @FXML
    ToggleButton changeSongs;
    @FXML
    ToggleButton changeArtists;

    private String songButtonState = "long_term";
    private String artistButtonState = "long_term";
    public void initialize() {
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey).getParentStage();
        setupWindowBarDrag();
        loadSongs("short_term");
        loadSongs("long_term");
        loadArtists("short_term");
        loadArtists("long_term");
        topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topBar.getScene();
                displaySongs();
                displayArtists();
            }
        });
    }

    public void loadSongs(String term) { // defaults recent songs
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

    public void displaySongs() {
        SpotifyUser tempSpotifyUser = App.session.getAppUser();
        tempSpotifyUser.getUserAuthorizationManager().refreshAuthCode();
        Paging<Track> trackPaging;
        if (songButtonState.equals("short_term")) {
            trackPaging = tempSpotifyUser.getTrackPagingLong();
            songButtonState = "long_term";
            changeSongs.setText("Show Recent");
        }
        else {
            trackPaging = tempSpotifyUser.getTrackPagingShort();
            songButtonState = "short_term";
            changeSongs.setText("Show All Time");
        }
        int size = trackPaging.getItems().length;
        if (size != 0)  {
            errorLabelSongs.setText("");
            changeSongs.setDisable(false);
        }
        for (int i = 0; i < size; i++) {
            Node node = currentScene.lookup("#songBackground" + i);
            node.setDisable(false);
            Track track = trackPaging.getItems()[i];
            Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 50,50, false, false);
            ((ImageView) currentScene.lookup("#topSongImage" + i)).setImage(trackImage);
            String trackName = track.getName();
            if (trackName.length() > 20) {
                trackName = trackName.substring(0,19) + "...";
            }
            ArtistSimplified[] artists = track.getArtists();
            StringBuilder allArtists = new StringBuilder("- ");
            for (int k = 0; k < artists.length; k++) {
                allArtists.append(artists[k].getName());
                if (k != artists.length - 1) allArtists.append(", ");
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
        }
        if (size == 0) {
            errorLabelSongs.setText("Oops.. looks like you have no top songs");
            //changeSongs.setDisable(true); bug fix --> change present on load artists, swapping between recent and all time, os dependent loading, top bar messed up for mac
        }
    }
    public void loadArtists(String term) { // defaults recent songs
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
        if (artistButtonState.equals("short_term")) {
            artistPaging = tempSpotifyUser.getArtistPagingLong();
            artistButtonState = "long_term";
            changeArtists.setText("Show Recent");
        }
        else {
            artistPaging = tempSpotifyUser.getArtistPagingShort();
            artistButtonState = "short_term";
            changeArtists.setText("Show All Time");
        }
        int size = artistPaging.getItems().length;
        if (size != 0) {
            errorLabelArtists.setText("");
            changeArtists.setDisable(false);
        }
        for (int i = 0; i < size; i++) {
            Node node = currentScene.lookup("#artistBackground" + i);
            node.setDisable(false);
            Artist artist = artistPaging.getItems()[i];
            Image artistImage = new Image(artist.getImages()[0].getUrl(), 50,50, false, false);
            ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
            String artistName = artist.getName();
            if (artistName.length() > 20) {
                artistName = artistName.substring(0,19) + "...";
            }
            ((Label) currentScene.lookup("#topArtistInfo" + i)).setText("  " + artistName);
            ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
        }
        for (int i = size; i < 10; i++) {
            Node node = currentScene.lookup("#artistBackground" + i);
            node.setDisable(true);
        }
        if (size == 0) {
            errorLabelArtists.setText("Oops.. looks like you have no top artists");
            //changeArtists.setDisable(true);
        }
    }

    public String popToString(int popularity) {
        if (popularity >= 90) return "Very Popular";
        else if (popularity >= 67) return "Popular";
        else if (popularity >= 45) return "Less Popular";
        else return "Not That Popular";
    }

}

