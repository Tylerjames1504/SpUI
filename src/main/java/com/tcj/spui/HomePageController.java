package com.tcj.spui;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import se.michaelthelin.spotify.model_objects.miscellaneous.AudioAnalysis;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
    private ToggleButton changeSongs;
    @FXML
    private Label errorLabelArtists;
    @FXML
    private Label errorLabelSongs;
    private String artistButtonState = "short_term";
    private String songButtonState = "short_term";
    private String[] topArtistPieces = {"artistHover", "topArtistInfo", "topArtistPopu","topArtistImage"};
    private int[] topArtistPiecesSizes = {10, 10, 10, 10};
    // per element hover functionality features top Artists
    private String[] topArtistPiecesMax = {"artistHover", "topArtistInfo", "topArtistPopu","topArtistImage","changeArtists"};
    private int[] topArtistPiecesMaxSizes = {10, 10, 10, 10, -1};
    // per element hover functionality features top Artists full set

    private String[] topSongPieces = {"songHover", "topSongInfo", "topSongPopu","topSongImage"};
    private int[] topSongPiecesSizes = {10, 10, 10, 10};
    // per element hover functionality features top Songs
    private String[] topSongPiecesMax = {"songHover", "topSongInfo", "topSongPopu","topSongImage","changeSongs"};
    private int[] topSongPiecesMaxSizes = {10, 10, 10, 10, -1};
    // per element hover functionality features top Songs full set
    private Paging<Track> trackPaging;
    private Paging<Artist> artistPaging;
    public void initialize() {
        this.spotifyApi = App.session.grabApi();
        topArtistsLabel.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topArtistsLabel.getScene();
                initializeArtists();
                initializeSongs();
            }
        });
    }
    public void initializeSongs() { // recent songs is default
        final GetUsersTopTracksRequest getUsersTopTracksRequest = this.spotifyApi.getUsersTopTracks()
                .limit(10)
                .offset(0)
                .time_range(songButtonState)
                .build();
        int size = 0;
        try {
            this.trackPaging = getUsersTopTracksRequest.execute();
            size = trackPaging.getItems().length;
            for (int i = 0; i < size; i++) {
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
                ((Label) currentScene.lookup("#topSongInfo" + i)).setText(trackName + "\n" + allArtists);
                ((Label) currentScene.lookup("#topSongPopu" + i)).setText(popToString(track.getPopularity()));
            }
        } catch (IOException | ParseException | SpotifyWebApiException  e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> disableSet = generateSet(0, size, topSongPiecesSizes, topSongPieces);
        if (size == 0) {
            disableSet = generateSet(0, size, topSongPiecesMaxSizes, topSongPiecesMax);
            errorLabelSongs.setText("Oops.. looks like you have no top songs");
        }
        disableSet(disableSet);
    }
    public void populateSongs() { // recent songs is default
        if (songButtonState.equals("short_term")) {
            songButtonState = "long_term";
            changeSongs.setText("Show Recent");
        } else {
            songButtonState = "short_term";
            changeSongs.setText("Show All Time");
        }
        final GetUsersTopTracksRequest getUsersTopTracksRequest = this.spotifyApi.getUsersTopTracks()
                .limit(10)
                .offset(0)
                .time_range(songButtonState)
                .build();
        int size = 0;
        try {
            this.trackPaging = getUsersTopTracksRequest.execute();
            size = trackPaging.getItems().length;
            for (int i = 0; i < size; i++) {
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
                ((Label) currentScene.lookup("#topSongInfo" + i)).setText(trackName + "\n" + allArtists);
                ((Label) currentScene.lookup("#topSongPopu" + i)).setText(popToString(track.getPopularity()));
            }
        } catch (IOException | ParseException | SpotifyWebApiException  e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> enableSet = generateSet(size,0, topSongPiecesSizes, topSongPieces); // UNIT test enable then swap size variable enable again etc.
        if (size != 0) {
            enableSet = generateSet(size, 0, topSongPiecesMaxSizes, topSongPiecesMax);
            errorLabelSongs.setText("");
        }
        enableSet(enableSet);
    }
    public void initializeArtists() { // recent artists is default
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = this.spotifyApi.getUsersTopArtists()
                .limit(10)
                .offset(0)
                .time_range(artistButtonState)
                .build();
        int size = 0;
        try {
            this.artistPaging = getUsersTopArtistsRequest.execute();
            size = artistPaging.getItems().length;
            for (int i = 0; i < size; i++) {
                Artist artist = artistPaging.getItems()[i];
                Image artistImage = new Image(artist.getImages()[0].getUrl(), 50,50, false, false);
                ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
                String artistName = artist.getName();
                if (artistName.length() > 20) {
                    artistName = artistName.substring(0,19) + "...";
                }
                ((Label) currentScene.lookup("#topArtistInfo" + i)).setText(artistName);
                ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
            }
        } catch (IOException | ParseException | SpotifyWebApiException  e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> disableSet = generateSet(0, size, topArtistPiecesSizes, topArtistPieces);
        if (size == 0) {
            disableSet = generateSet(0, size, topArtistPiecesMaxSizes, topArtistPiecesMax);
            errorLabelArtists.setText("Oops.. looks like you have no top artists");
        }
        disableSet(disableSet);
    }
    public void populateArtists(ActionEvent event) {
        if (artistButtonState.equals("short_term")) {
            artistButtonState = "long_term";
            changeArtists.setText("Show Recent");
        } else {
            artistButtonState = "short_term";
            changeArtists.setText("Show All Time");
        }
        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = this.spotifyApi.getUsersTopArtists()
                .limit(10)
                .offset(0)
                .time_range(artistButtonState)
                .build();
        int size = 0;
        try {
            this.artistPaging = getUsersTopArtistsRequest.execute();
            size = artistPaging.getItems().length;
            for (int i = 0; i < size; i++) {
                Artist artist = artistPaging.getItems()[i];
                Image artistImage = new Image(artist.getImages()[0].getUrl(), 50, 50, false, false);
                ((ImageView) currentScene.lookup("#topArtistImage" + i)).setImage(artistImage);
                String artistName = artist.getName();
                if (artistName.length() > 20) {
                    artistName = artistName.substring(0,19) + "...";
                }
                ((Label) currentScene.lookup("#topArtistInfo" + i)).setText(artistName);
                ((Label) currentScene.lookup("#topArtistPopu" + i)).setText(popToString(artist.getPopularity()));
            }
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> enableSet = generateSet(size,0, topArtistPiecesSizes, topArtistPieces); // UNIT test enable then swap size variable enable again etc.
        if (size != 0) {
            enableSet = generateSet(size, 0, topArtistPiecesMaxSizes, topArtistPiecesMax);
            errorLabelArtists.setText("");
        }
        enableSet(enableSet);
    }
    public void onClickTopArtistsSongs(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        int index = Integer.parseInt(sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        try {
            if (sourceInfo[1].toLowerCase().contains("song")) Desktop.getDesktop().browse(new URI(trackPaging.getItems()[index].getExternalUrls().get("spotify")));
            if (sourceInfo[1].toLowerCase().contains("artist")) Desktop.getDesktop().browse(new URI(artistPaging.getItems()[index].getExternalUrls().get("spotify")));
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public void onHoverTopArtistsSongs(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String songOrArtist = "";
        if (sourceInfo[1].toLowerCase().contains("song")) songOrArtist = "song";
        if (sourceInfo[1].toLowerCase().contains("artist")) songOrArtist = "artist";
        Region selected = (Region) currentScene.lookup("#" + songOrArtist + "Hover" + sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        selected.setStyle("-fx-background-color: #323436");
    }
    public void offHoverTopArtistsSongs(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String songOrArtist = "";
        if (sourceInfo[1].toLowerCase().contains("song")) songOrArtist = "song";
        if (sourceInfo[1].toLowerCase().contains("artist")) songOrArtist = "artist";
        Region selected = (Region) currentScene.lookup("#" + songOrArtist + "Hover" + sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        selected.setStyle("-fx-background-color: transparent");
    }
    public void underline(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String type = sourceInfo[0];
        String fxid = sourceInfo[1];
        if (type.equals("Label")) {
            Label label = (Label) currentScene.lookup(fxid);
            label.setUnderline(true);
        }
        if (type.equals("ToggleButton")) {
            ToggleButton toggleButton = (ToggleButton) currentScene.lookup(fxid);
            toggleButton.setUnderline(true);
        }
        if (type.equals("Button")) {
            Button button = (Button) currentScene.lookup(fxid);
            button.setUnderline(true);
        }

    }

    public void resetUnderline(MouseEvent event) { //usage onMouseExited (add elements below if needed)
        String[] sourceInfo = parseSource(event.getSource());
        String type = sourceInfo[0];
        String fxid = sourceInfo[1];
        if (type.equals("Label")) {
            Label label = (Label) currentScene.lookup(fxid);
            label.setUnderline(false);
        }
        if (type.equals("ToggleButton")) {
            ToggleButton toggleButton = (ToggleButton) currentScene.lookup(fxid);
            toggleButton.setUnderline(false);
        }
        if (type.equals("Button")) {
            Button button = (Button) currentScene.lookup(fxid);
            button.setUnderline(false);
        }
    }
    public String popToString(int popularity) {
        if (popularity >= 90) return "Very Popular";
        else if (popularity >= 67) return "Popular";
        else if (popularity >= 45) return "Less Popular";
        else return "Not That Popular";
    }
    public String[] parseSource(Object source) {
        String type = source.getClass().toString();
        String[] typeBreakdown = type.split("[.]");
        type = typeBreakdown[typeBreakdown.length - 1];

        String fxid = source.toString();
        String[] findSource = fxid.split(",");
        fxid = fxid.substring(fxid.indexOf("id=") + 3, findSource[0].length());
        if (findSource[0].charAt(findSource[0].length()- 1) ==  ']') {
            fxid = fxid.substring(0, fxid.length() - 1);
        }

        return new String[] {type, "#" + fxid};
    }
    public ArrayList<String> generateSet(int mode, int anchor, int[] basesSetSizes, String[] bases) { //mode 0; top is variable, mode not 0; top is set to value
        ArrayList<String> outputSet = new ArrayList();
        for (int i = 0; i < basesSetSizes.length; i++) {
            if (basesSetSizes[i] == -1) outputSet.add("#" + bases[i]);
            else {
                if (mode == 0) {
                    for (int j = anchor; j < basesSetSizes[i]; j++) {
                        outputSet.add("#" + bases[i] + j);
                    }
                }
                else {
                    for (int j = anchor; j < mode; j++) {
                        outputSet.add("#" + bases[i] + j);
                    }
                }
            }
        }
        return outputSet;
    }
    public void disableSet(ArrayList<String> set) {
        for (int i = 0; i < set.size(); i++) {
             Node node = currentScene.lookup(set.get(i));
             node.setDisable(true);
        }
    }
    public void enableSet(ArrayList<String> set) {
        for (int i = 0; i < set.size(); i++) {
            Node node = currentScene.lookup(set.get(i));
            node.setDisable(false);
        }
    }


}
