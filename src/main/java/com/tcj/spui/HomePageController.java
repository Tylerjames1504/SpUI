package com.tcj.spui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import java.io.IOException;

public class HomePageController {
    private SpotifyApi spotifyApi;
    private Scene currentScene;
    @FXML
    private Label topArtistsLabel;

    public void initialize() {
        this.spotifyApi = App.session.grabApi();
        topArtistsLabel.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topArtistsLabel.getScene();
                initializeArtists();
            }
        });
    }
    public void initializeArtists() { // recent artists is default
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = this.spotifyApi.getUsersTopArtists()
                .limit(10)
                .offset(0)
                .time_range("short_term")
                .build();
        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            Artist topArtist = artistPaging.getItems()[0];

            Image artistImage = new Image(topArtist.getImages()[0].getUrl());


            ((ImageView) currentScene.lookup("#topArtistImage0")).setImage(artistImage);

            for (Artist artist : artistPaging.getItems()) {
                System.out.println(artist.getName());
            }

        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void populateRecentTopArtists(ActionEvent event) {

    }
}
