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
import se.michaelthelin.spotify.requests.data.albums.GetAlbumRequest;
import se.michaelthelin.spotify.requests.data.albums.GetAlbumsTracksRequest;
import se.michaelthelin.spotify.requests.data.browse.GetListOfNewReleasesRequest;
import se.michaelthelin.spotify.requests.data.browse.GetRecommendationsRequest;
import se.michaelthelin.spotify.requests.data.browse.miscellaneous.GetAvailableGenreSeedsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioAnalysisForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.List;


/*TODO, currently refreshing the access token in this class whenever the user is clicking (ctrl + f : "App.session.refreshAuthCode();") to find instances
 TODO Will need to do this automatically to avoid timeout and 401 error */

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

    private String[] discovPieces = {"discovHover", "discovInfo","discovImage"};
    private int[] discovPiecesSizes = {8, 8, 8};
    private String[] discovPiecesMax = {"discovHover", "discovInfo","discovImage"}; //add refresh
    private int[] discovPiecesMaxSizes = {8, 8, 8}; // add -1
    private Paging<Track> trackPaging;
    private Paging<Artist> artistPaging;
    private ArrayList<TrackSimplified> discoveryPool = new ArrayList();
    private Track[] discoveryShown = new Track[8];

//    This method is called after the Scene of this controller is completely set. -> Inits artists, songs, and discovery
    public void initialize() {
        this.spotifyApi = App.session.grabApi();
        topArtistsLabel.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topArtistsLabel.getScene();
                initializeArtists();
                initializeSongs();
                initializeDiscovery();
            }
        });
    }

//    Initializes the discovery section of the main page using a pool of recommended tracks and new popular tracks
    public void initializeDiscovery() {
        String artistsBuilder = "";
        String tracksBuilder = "";
        for (int i = 0; i < artistPaging.getItems().length; i++) {
            if (i == 3) break;
            artistsBuilder += artistPaging.getItems()[i].getId();
            if (i < 2 && i != artistPaging.getItems().length) artistsBuilder += ",";
        }
        for (int i = 0; i < trackPaging.getItems().length; i++) {
            if (i == 2) break;
            tracksBuilder += trackPaging.getItems()[i].getId();
            if (i < 1 && i != trackPaging.getItems().length) tracksBuilder += ",";
        }
        final GetRecommendationsRequest getRecommendationsRequest = this.spotifyApi.getRecommendations()
                .limit(20)
                .seed_artists(artistsBuilder)
                .seed_tracks(tracksBuilder)
                .build();
        final GetListOfNewReleasesRequest getListOfNewReleasesRequest = this.spotifyApi.getListOfNewReleases()
                .limit(10)
                .build();
        int size = 0;
        try {
            List<TrackSimplified> recommendedTracks = new ArrayList();
            if (artistPaging.getItems().length > 0) {
                Recommendations recommendations = getRecommendationsRequest.execute();
                recommendedTracks = Arrays.asList(recommendations.getTracks());
            }
            Paging<AlbumSimplified> newReleases = getListOfNewReleasesRequest.execute();
            for (int i = 0; i < newReleases.getItems().length; i++) {
                Paging<TrackSimplified> albumsTracks = this.spotifyApi.getAlbumsTracks(newReleases.getItems()[i].getId()).build().execute();
                discoveryPool.add(albumsTracks.getItems()[0]);
            }
            for (int i = 0; i < recommendedTracks.size(); i++) {
                discoveryPool.add(recommendedTracks.get(i));
            }
            int bound = discoveryPool.size();
            size = bound;
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                if (i == 8) break;
                int index = random.nextInt(0,bound);
                Track track = this.spotifyApi.getTrack(discoveryPool.get(index).getId()).build().execute();
                this.discoveryShown[i] = track;
                Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 50,50, false, false);
                ((ImageView) currentScene.lookup("#discovImage" + i)).setImage(trackImage);
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
                ((Label) currentScene.lookup("#discovInfo" + i)).setText(trackName + "\n" + allArtists);
                discoveryPool.remove(index);
                bound--;
            }

        }
        catch (IOException | ParseException | SpotifyWebApiException  e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> disableSet = generateSet(0, size, discovPiecesSizes, discovPieces);
        if (size == 0) {
            disableSet = generateSet(0, size, discovPiecesMaxSizes, discovPiecesMax);
            //errorLabelSongs.setText("Oops.. looks like there are no recommended songs or releases"); // error label
        }
        disableSet(disableSet);
    }
//    Initializes the top songs section of the main page using the users top 10 songs
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
//    When the button to swap between recent and all time songs is pressed, this method is called. Reacts to the button state and swaps the songs accordingly
    public void populateSongs() { // recent songs is default
        App.session.refreshAuthCode();
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
//    Initializes the top artists section of the main page using the users top 10 artists
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
//    When the button to swap between recent and all time artists is pressed, this method is called. Reacts to the button state and swaps the artists accordingly
    public void populateArtists(ActionEvent event) {
        App.session.refreshAuthCode();
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
//    Generic method that opens up an artist and songs link when it is clicked on in the gui
    public void onClickExpandable(MouseEvent event) {
        App.session.refreshAuthCode();
        String[] sourceInfo = parseSource(event.getSource());
        int index = Integer.parseInt(sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            try {
                if (sourceInfo[1].toLowerCase().contains("song")) Runtime.getRuntime().exec("xdg-open " + trackPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("artist")) Runtime.getRuntime().exec("xdg-open " + artistPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("discov")) {
//                    Runtime.getRuntime().exec("xdg-open " + discoveryShown[index].getArtists()[0].getExternalUrls().get("spotify"));
                    Runtime.getRuntime().exec("xdg-open " + discoveryShown[index].getExternalUrls().get("spotify"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (os.contains("mac")) {
            try {
                if (sourceInfo[1].toLowerCase().contains("song")) Runtime.getRuntime().exec("open " + trackPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("artist")) Runtime.getRuntime().exec("open " + artistPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("discov")) {
//                    Runtime.getRuntime().exec("open " + discoveryShown[index].getArtists()[0].getExternalUrls().get("spotify"));
                    Runtime.getRuntime().exec("open " + discoveryShown[index].getExternalUrls().get("spotify"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (os.contains("win")) {
            try {
                if (sourceInfo[1].toLowerCase().contains("song")) Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + trackPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("artist")) Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + artistPaging.getItems()[index].getExternalUrls().get("spotify"));
                if (sourceInfo[1].toLowerCase().contains("discov")) {
//                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + discoveryShown[index].getArtists()[0].getExternalUrls().get("spotify"));
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + discoveryShown[index].getExternalUrls().get("spotify"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        try {
//            App.session.refreshAuthCode();
//            if (sourceInfo[1].toLowerCase().contains("song")) Desktop.getDesktop().browse(new URI(trackPaging.getItems()[index].getExternalUrls().get("spotify")));
//            if (sourceInfo[1].toLowerCase().contains("artist")) Desktop.getDesktop().browse(new URI(artistPaging.getItems()[index].getExternalUrls().get("spotify")));
//            if (sourceInfo[1].toLowerCase().contains("discov")) {
//                Desktop.getDesktop().browse(new URI(discoveryShown[index].getArtists()[0].getExternalUrls().get("spotify")));
//                Desktop.getDesktop().browse(new URI(discoveryShown[index].getExternalUrls().get("spotify")));
//            }
//        }
//        catch (IOException | URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
    }
//    Generic method that changes the color of the background behind elements when they are hovered over
    public void onHoverExpandable(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String type = "";
        if (sourceInfo[1].toLowerCase().contains("song")) type = "song";
        if (sourceInfo[1].toLowerCase().contains("artist")) type = "artist";
        if (sourceInfo[1].toLowerCase().contains("discov")) type = "discov";
        Region selected = (Region) currentScene.lookup("#" + type + "Hover" + sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        selected.setStyle("-fx-background-color: #323436");
    }
//    Generic method that changes back the color of the background behind elements when they are no longer hovered over
    public void offHoverExpandable(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String type = "";
        if (sourceInfo[1].toLowerCase().contains("song")) type = "song";
        if (sourceInfo[1].toLowerCase().contains("artist")) type = "artist";
        if (sourceInfo[1].toLowerCase().contains("discov")) type = "discov";
        Region selected = (Region) currentScene.lookup("#" + type + "Hover" + sourceInfo[1].substring(sourceInfo[1].length() - 1, sourceInfo[1].length()));
        selected.setStyle("-fx-background-color: transparent");
    }
//    Underlines the text of the Swap button when hovered over
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
//    Undoes the underlining of the text when the mouse is no longer hovering over the button
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
//    Given a popularity number referring to a track or artist, returns a string that represents the popularity
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
