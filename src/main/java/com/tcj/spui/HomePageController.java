package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.io.IOException;

public class HomePageController extends SceneUtilities {
    @FXML
    Label errorLabelSongs;
    public void initialize() {
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey).getParentStage();
        setupWindowBarDrag();
        topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topBar.getScene();
                initializeSongs("short_term");
                initializeSongs("long_term");
            }
        });
    }

    public void initializeSongs(String term) {
        final GetUsersTopTracksRequest getUsersTopTracksRequest = this.spotifyApi.getUsersTopTracks()
                .limit(10)
                .offset(0)
                .time_range(term)
                .build();
        int size = 0;
        User tempUser = App.session.getAppUser();
        try {
            if (term.equals("short_term")) {
                tempUser.setTrackPagingShort(getUsersTopTracksRequest.execute());
                size = tempUser.getTrackPagingShort().getItems().length;
            }
            if (term.equals("long_term")) {
                tempUser.setTrackPagingLong(getUsersTopTracksRequest.execute());
                size = tempUser.getTrackPagingLong().getItems().length;
            }
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
        for (int i = size; i < 10; i++) {
            Node node = currentScene.lookup("#songBackground" + i);
            node.setDisable(true);
        }
        if (size == 0) {
            errorLabelSongs.setText("Oops.. looks like you have no top songs");
        }
        else if (term.equals("long_term")) displaySongs("long_term");

    }
    public void displaySongs(String term) {
        User tempUser = App.session.getAppUser();
        Paging<Track> trackPaging = null;
        if (term.equals("short_term")) {
            trackPaging = tempUser.getTrackPagingShort();
        }
        if (term.equals("long_term")) {
            trackPaging = tempUser.getTrackPagingLong();
        }
        int size = trackPaging.getItems().length;
        if (size != 0) errorLabelSongs.setText("");
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
            String allArtists = "- ";
            for (int k = 0; k < artists.length; k++) {
                allArtists += artists[k].getName();
                if (k != artists.length - 1) allArtists += ", ";
            }
            if (allArtists.length() > 20) {
                allArtists = allArtists.substring(0,19) + "...";
            }
            ((Label) currentScene.lookup("#topSongInfo" + i)).setText("  " + trackName + "\n  " + allArtists);
            ((Label) currentScene.lookup("#topSongPopu" + i)).setText(popToString(track.getPopularity()));
        }
    }

    public String popToString(int popularity) {
        if (popularity >= 90) return "Very Popular";
        else if (popularity >= 67) return "Popular";
        else if (popularity >= 45) return "Less Popular";
        else return "Not That Popular";
    }

}

