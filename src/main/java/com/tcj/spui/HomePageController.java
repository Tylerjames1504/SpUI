package com.tcj.spui;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class HomePageController {
    private SpotifyApi spotifyApi;
    private Scene currentScene;
    @FXML
    private Label topArtistsLabel;
    @FXML
    private ToggleButton changeArtists;
    @FXML
    private Label errorLabelArtists;
    private String buttonState = "short_term";
    private int disableTopArtistFunctionality = 0;


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
                .time_range(buttonState)
                .build();
        try {
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            for (int i = 0; i < 10; i++) {
                Artist artist = artistPaging.getItems()[i];
                Image artistImage = new Image(artist.getImages()[0].getUrl(), 50,50, false, false);
                ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
                String artistName = artist.getName();
                if (artistName.length() > 50) {
                    artistName = artistName.substring(0,19) + "...";
                }
                ((Label) currentScene.lookup("#topArtistInfo" + i)).setText(artistName);
                ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
            }
        } catch (IOException | ParseException | SpotifyWebApiException  e) {
            throw new RuntimeException(e);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorLabelArtists.setText("Oops.. looks like you have no top artists");
            disableTopArtistFunctionality = 1; // disables functionality
            this.changeArtists.setText("");
        }
    }
    public void onHover(MouseEvent event) {
        if (disableTopArtistFunctionality == 0) {
            String eventString = event.getSource().toString();
            String lookup = "";
            if (eventString.charAt(0) == 'L') {
                lookup = eventString.substring(22,23);
            }
            else if (eventString.charAt(0) == 'R') {
                lookup = eventString.substring(eventString.length() - 2, eventString.length() - 1);
            }
            else if (eventString.charAt(0) == 'I') {
                lookup = eventString.substring(27,28);
            }
            Region selected = (Region) currentScene.lookup("#artistHover" + lookup);
            selected.setStyle("-fx-background-color: #323436");
        }
    }
    public void offHover(MouseEvent event) {
        if (disableTopArtistFunctionality == 0) {
            String eventString = event.getSource().toString();
            String lookup = "";
            if (eventString.charAt(0) == 'L') {
                lookup = eventString.substring(22,23);
            }
            else if (eventString.charAt(0) == 'R') {
                lookup = eventString.substring(eventString.length() - 2, eventString.length() - 1);
            }
            else if (eventString.charAt(0) == 'I') {
                lookup = eventString.substring(27,28);
            }
            Region selected = (Region) currentScene.lookup("#artistHover" + lookup);
            selected.setStyle("-fx-background-color: transparent");
        }
    }
    public void underline(MouseEvent event) {
        if (disableTopArtistFunctionality == 0) {
            changeArtists.setUnderline(true);
        }
    }
    public void reset(MouseEvent event) {
        if (disableTopArtistFunctionality == 0) {
            changeArtists.setUnderline(false);
        }
    }
    public String popToString(int popularity) {
        if (popularity >= 90) return "Very Popular";
        else if (popularity >= 67) return "Popular";
        else if (popularity >= 45) return "Less Popular";
        else return "Not That Popular";
    }
    public void populateArtists(ActionEvent event) {
        if (disableTopArtistFunctionality == 0) {
            if (buttonState.equals("short_term")) {
                buttonState = "long_term";
                changeArtists.setText("Show Recent");
            } else {
                buttonState = "short_term";
                changeArtists.setText("Show All Time");
            }
            final GetUsersTopArtistsRequest getUsersTopArtistsRequest = this.spotifyApi.getUsersTopArtists()
                    .limit(10)
                    .offset(0)
                    .time_range(buttonState)
                    .build();
            try {
                final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
                for (int i = 0; i < 10; i++) {
                    Artist artist = artistPaging.getItems()[i];
                    Image artistImage = new Image(artist.getImages()[0].getUrl(), 50, 50, false, false);
                    ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
                    String artistName = artist.getName();
                    if (artistName.length() > 50) {
                        artistName = artistName.substring(0,19) + "...";
                    }
                    ((Label) currentScene.lookup("#topArtistInfo" + i)).setText(artistName);
                    ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
                }
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
