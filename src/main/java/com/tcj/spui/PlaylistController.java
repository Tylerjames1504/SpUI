package com.tcj.spui;

import com.neovisionaries.i18n.CountryCode;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.SnapshotResult;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.CreatePlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlaylistController extends SceneUtilities {
    @FXML
    ImageView rightButton;
    @FXML
    ImageView leftButton;
    @FXML
    Label genreSpliceDesc;
    @FXML
    Label randomizeDesc;
    private PlaylistBlockCycle playlistHead;
    private PlaylistBlockCycle absoluteHead;
    private PlaylistData currentSelected;
    private String currentPlaylistState = "";
    private String currentPlaylistEditState = "";
    private Track[] artistTrack;
    private ArrayList<Track> discoveryPlaylistPool = new ArrayList();
    private ArrayList<Track> newReleasesPool = new ArrayList();

    private ArrayList<Track> genreSplicing = new ArrayList();
    private String currentSplicedGenre = "Genre";

    public void initialize() {
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey)
                .getParentStage();
        // load info
        loadPlaylists();
        topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setupWindowBarDrag();
                this.currentScene = topBar.getScene();
                // display info
                displayPlaylists();
            }
        });
    }
    public void displayPlaylists() {
        for (int i = 0; i < 4; i++) {
            if (playlistHead.block[i] != null) {
                Node node = currentScene.lookup("#playlistBackground" + i);
                node.setDisable(false);
                node.setVisible(true);
                Pane pane = (Pane)currentScene.lookup("#playlistBackground" + i);
                if (!(playlistHead.block[i].selected)) {
                    pane.getStyleClass().remove("selected");
                } else {
                    pane.getStyleClass().add("selected");
                }
                Image image = new Image(playlistHead.block[i].thisPlaylist.getImages()[0].getUrl(), 128, 128, false, false);
                ((ImageView)currentScene.lookup("#playListImage" + i)).setImage(image);
                String text = " " + playlistHead.block[i].thisPlaylist.getName();
                String access = "";
                if (playlistHead.block[i].thisPlaylist.getIsPublicAccess()) {
                    access = "Public";
                } else { access = "Private"; }
                text += "\n " + access;
                text += "\n " + playlistHead.block[i].playlistInfo.getTotal() + " songs";

                ((Label)currentScene.lookup("#playlistInfo" + i)).setText(text);
            }
            else {
                Node node = currentScene.lookup("#playlistBackground" + i);
                node.setDisable(true);
                node.setVisible(false);
            }
        }

    }
    public void loadPlaylists() {
        // no playlist error --> incomplete pages too & disabling arrow buttons
        // refresh playlists using hashmap for genre and using playlist id
        // central storage of playlist chain may not be necessary as scenes using stage manager are never unloaded
        try {
            PlaylistBlockCycle head = new PlaylistBlockCycle();
            PlaylistBlockCycle temp = head;
            final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = App.session.getAppUser()
                    .getUserAuthorizationManager()
                    .getRetrievedApi()
                    .getListOfCurrentUsersPlaylists()
                    .limit(35)
                    .build();
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = getListOfCurrentUsersPlaylistsRequest.execute();
            int k = 0;
            for (int i = 0; i < playlistSimplifiedPaging.getItems().length; i++) {
                PlaylistData tempData = new PlaylistData(playlistSimplifiedPaging.getItems()[i].getId(), k);
                temp.block[k] = tempData;
                if (i == 0) {
                    this.currentSelected = tempData;
                    tempData.selected = true;
                }
                k++;
                if (k == 4 && i != playlistSimplifiedPaging.getItems().length - 1) {
                    PlaylistBlockCycle newPlaylistBlockCycle = new PlaylistBlockCycle();
                    newPlaylistBlockCycle.previous = temp;
                    temp.next = newPlaylistBlockCycle;
                    temp = temp.next;
                    k = 0;
                }
            }
            temp.next = head;
            head.previous = temp;
            this.playlistHead = head;
            this.absoluteHead = head;

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e);
        }

    }
    public void swapScene(){
        swapScene("homeScene", "Home");
    }
    public void hover(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String currentEvent = event.getEventType().toString();
        if (sourceInfo[1].toLowerCase().contains("right")) {
            Image image;
            if (currentEvent.equals("MOUSE_EXITED")) {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("right_arrow_static.png")));
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("right_arrow_hover.png")));
            }
            rightButton.setImage(image);
        }
        if (sourceInfo[1].toLowerCase().contains("left")) {
            Image image;
            if (currentEvent.equals("MOUSE_EXITED")) {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("left_arrow_static.png")));
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("left_arrow_hover.png")));
            }
            leftButton.setImage(image);
        }
        if (sourceInfo[1].toLowerCase().contains("plusbutton")) {
            Image image;
            int index = Integer.parseInt(sourceInfo[1].substring(sourceInfo[1].length() - 1));
            if (currentEvent.equals("MOUSE_EXITED")) {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("add_static.png")));
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("add_hover.png")));
            }

            ((ImageView) currentScene.lookup("#plusbutton" + index)).setImage(image);
        }
    }
    public void movePage(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        if (sourceInfo[1].toLowerCase().contains("left")) {
            playlistHead = playlistHead.previous;
        }
        if (sourceInfo[1].toLowerCase().contains("right")) {
            playlistHead = playlistHead.next;
        }
        displayPlaylists();
    }

    public void setSelected(MouseEvent event) {
        String[] sourceInfo = parseSource(event);
        int index = Integer.parseInt(sourceInfo[1].substring(sourceInfo[1].length() - 1));
        if (playlistHead.block[index] == this.currentSelected) return;
        for (int i = 0; i < 4; i++) {
            if (playlistHead.block[i] != null) {
                Pane pane = (Pane) currentScene.lookup("#playlistBackground" + i);
                if (i != index) {
                    pane.getStyleClass().remove("selected");
                    playlistHead.block[i].selected = false;
                } else {
                    pane.getStyleClass().add("selected");
                    playlistHead.block[i].selected = true;
                }
            }
        }
        this.currentSelected = playlistHead.block[index];
        if (currentPlaylistEditState.equals("randomize")) {
            buildRandomization();
        }
        if (currentPlaylistEditState.equals("genre")) {
            buildGenreSplicing();
        }


        PlaylistBlockCycle temp = playlistHead.next;
        while (temp != playlistHead && temp != null) {
            for (int i = 0; i < 4; i++) {
                if (temp.block[i] != null) {
                    temp.block[i].selected = false;
                }
            }
            temp = temp.next;
        }

    }
    public void buildBottomPlaylist(MouseEvent event) {
        Playlist playlist = null;
        SpotifyApi spotifyApi = App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi();
        if (currentPlaylistState.equals("topartists")) {
            JSONObject json;
            try {
                json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            String id = json.get("id").toString();
            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(id, "Top Artists")
                    .public_(false)
                    .description("Playlist created from your top artists")
                    .build();
            try {
                playlist = createPlaylistRequest.execute();
                String[] uris = new String[artistTrack.length];
                for (int i = 0; i < artistTrack.length; i++) {
                    uris[i] = artistTrack[i].getUri();
                }
                SnapshotResult snapshotResult = spotifyApi
                        .addItemsToPlaylist(playlist.getId(), uris)
                        .build()
                        .execute();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            this.currentPlaylistState = "";
            Pane pane = (Pane) currentScene.lookup("#topartistsPane");
            pane.getStyleClass().remove("selected");
            clearBottomPreview();
        }
        if (currentPlaylistState.equals("discovery")) {
            JSONObject json;
            try {
                json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            String id = json.get("id").toString();
            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(id, "Discovery")
                    .public_(false)
                    .description("Playlist created from your discovery")
                    .build();
            try {
                playlist = createPlaylistRequest.execute();
                String[] uris = new String[discoveryPlaylistPool.size()];
                for (int i = 0; i < discoveryPlaylistPool.size(); i++) {
                    uris[i] = discoveryPlaylistPool.get(i).getUri();
                }
                SnapshotResult snapshotResult = spotifyApi
                        .addItemsToPlaylist(playlist.getId(), uris)
                        .build()
                        .execute();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            Pane pane = (Pane) currentScene.lookup("#discoveryPane");
            pane.getStyleClass().remove("selected");
            clearBottomPreview();
        }
        if (currentPlaylistState.equals("newreleases")) {
            JSONObject json;
            try {
                json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            String id = json.get("id").toString();
            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(id, "New Releases")
                    .public_(false)
                    .description("Playlist created from new releases")
                    .build();
            try {
                playlist = createPlaylistRequest.execute();
                String[] uris = new String[newReleasesPool.size()];
                for (int i = 0; i < newReleasesPool.size(); i++) {
                    uris[i] = newReleasesPool.get(i).getUri();
                }
                SnapshotResult snapshotResult = spotifyApi
                        .addItemsToPlaylist(playlist.getId(), uris)
                        .build()
                        .execute();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            Pane pane = (Pane) currentScene.lookup("#newreleasesPane");
            pane.getStyleClass().remove("selected");
            clearBottomPreview();
        }
        if (currentPlaylistState.equals("topsongs")) {
            JSONObject json;
            try {
                json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            String id = json.get("id").toString();
            CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(id, "Top Songs")
                    .public_(false)
                    .description("Playlist created from your top songs")
                    .build();
            try {
                playlist = createPlaylistRequest.execute();
                String[] uris = new String[App.session.getAppUser().getTrackPagingShort().getItems().length];
                for (int i = 0; i < App.session.getAppUser().getTrackPagingShort().getItems().length; i++) {
                    uris[i] = App.session.getAppUser().getTrackPagingShort().getItems()[i].getUri();
                }
                SnapshotResult snapshotResult = spotifyApi
                        .addItemsToPlaylist(playlist.getId(), uris)
                        .build()
                        .execute();

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                throw new RuntimeException(e);
            }
            Pane pane = (Pane) currentScene.lookup("#topsongsPane");
            pane.getStyleClass().remove("selected");
            clearBottomPreview();
        }
        if (playlist != null) {
            addPlaylistToChain(playlist);
        }
    }

    public void clearBottomPreview() {
        this.currentPlaylistState = "";
        for (int i = 0; i < 8; i++) {
            ((ImageView) currentScene.lookup("#bottomPreviewImage" + i)).setImage(null);
            ((Label) currentScene.lookup("#bottomPreviewLabel" + i)).setText("");
        }
    }
    public void clearTopPreview() {
        this.currentPlaylistEditState = "";
        for (int i = 0; i < 8; i++) {
            ((ImageView) currentScene.lookup("#topPreviewImage" + i)).setImage(null);
            ((Label) currentScene.lookup("#topPreviewInfo" + i)).setText("");
        }
    }
    public void addPlaylistToChain(Playlist playlist) {
        PlaylistBlockCycle temp = this.absoluteHead.previous;

        for (int i = 0; i < 4; i++) {
            if (temp.block[i] == null) {
                temp.block[i] = new PlaylistData(playlist.getId(), i);
                displayPlaylists();
                return;
            }
        }
        temp = new PlaylistBlockCycle();
        temp.block[0] = new PlaylistData(playlist.getId(), 0);
        PlaylistBlockCycle holder = this.absoluteHead.previous;
        holder.next = temp;
        temp.previous = holder;
        temp.next = this.absoluteHead;
        this.absoluteHead.previous = temp;
        displayPlaylists();

    }
    public void loadBottomPreview(int type) {
        if (type == 1) {
            Paging<Artist> artists = App.session.getAppUser().getArtistPagingShort();
            int size = artists.getItems().length;
            for (int i = 0; i < size; i++) {
                if (i == 8) break;
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(false);
                node.setVisible(true);
                Artist artist = artists.getItems()[i];
                GetArtistsTopTracksRequest getArtistsTopTracksRequest = spotifyApi
                        .getArtistsTopTracks(artist.getId(), CountryCode.US)
                        .build();
                Track[] artistTrack = null;
                try {
                     artistTrack = getArtistsTopTracksRequest.execute();
                     this.artistTrack = artistTrack;
                }
                catch (IOException | SpotifyWebApiException | ParseException e) {
                    throw new RuntimeException(e);
                }
                Image artistImage = new Image(artist.getImages()[0].getUrl(), 67, 67, false, false);
                ((ImageView) currentScene.lookup("#bottomPreviewImage" + i)).setImage(artistImage);
                String artistName = artist.getName();
                Label label = ((Label) currentScene.lookup("#bottomPreviewLabel" + i));
                if (!(label.getStyleClass().contains("text"))){
                    label.getStyleClass().add("text");
                }
                label.setText("  " + artistName + "\n  " + artistTrack[0].getName());
            }
            for (int i = size; i < 8; i++) {
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(true);
                node.setVisible(false);
            }
            // if size 0 preview button indicates error
        }
        if (type == 2) {
            ArrayList<TrackSimplified> tracks = App.session.getAppUser().getDiscoveryPool();
            this.discoveryPlaylistPool.clear();
            int size = tracks.size();
            for (int i = 0; i < tracks.size(); i++) {
                if (i == 8) {
                    Track track1;
                    Track track2;
                    try {
                         track1 = this.spotifyApi.getTrack(App.session.getAppUser().getDiscoveryPool().get(i).getId()).build().execute();
                         track2 = this.spotifyApi.getTrack(App.session.getAppUser().getDiscoveryPool().get(i).getId()).build().execute();
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.discoveryPlaylistPool.add(track1);
                    this.discoveryPlaylistPool.add(track2);
                    break;
                }
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(false);
                node.setVisible(true);
                Track track = null;
                try {
                    track = this.spotifyApi.getTrack(App.session.getAppUser().getDiscoveryPool().get(i).getId()).build().execute();
                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    throw new RuntimeException(e);
                }
                this.discoveryPlaylistPool.add(track);
                Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 67, 67, false, false);
                ((ImageView) currentScene.lookup("#bottomPreviewImage" + i)).setImage(trackImage);
                String trackName = track.getName();
                ArtistSimplified[] artists = track.getArtists();
                StringBuilder allArtists = new StringBuilder("- ");
                for (int k = 0; k < artists.length; k++) {
                    allArtists.append(artists[k].getName());
                    if (k != artists.length - 1) {
                        allArtists.append(", ");
                    }
                }
                Label label = ((Label) currentScene.lookup("#bottomPreviewLabel" + i));
                if (!(label.getStyleClass().contains("text"))){
                    label.getStyleClass().add("text");
                }
                label.setText("  " + trackName + "\n  " + allArtists);
            }
            for (int i = size; i < 8; i++) {
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(true);
                node.setVisible(false);
            }
        }
        if (type == 3) {
            ArrayList<TrackSimplified> tracks = App.session.getAppUser().getNewReleases();
            this.newReleasesPool.clear();
            int size = tracks.size();
            for (int i = 0; i < tracks.size(); i++) {
                if (i == 8) {
                    Track track1;
                    Track track2;
                    try {
                        track1 = this.spotifyApi.getTrack(App.session.getAppUser().getNewReleases().get(i).getId()).build().execute();
                        track2 = this.spotifyApi.getTrack(App.session.getAppUser().getNewReleases().get(i).getId()).build().execute();
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                    this.newReleasesPool.add(track1);
                    this.newReleasesPool.add(track2);
                    break;
                }
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(false);
                node.setVisible(true);
                Track track = null;
                try {
                    track = this.spotifyApi.getTrack(App.session.getAppUser().getNewReleases().get(i).getId()).build().execute();
                } catch (IOException | SpotifyWebApiException | ParseException e) {
                    throw new RuntimeException(e);
                }
                this.newReleasesPool.add(track);
                Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 67, 67, false, false);
                ((ImageView) currentScene.lookup("#bottomPreviewImage" + i)).setImage(trackImage);
                String trackName = track.getName();
                ArtistSimplified[] artists = track.getArtists();
                StringBuilder allArtists = new StringBuilder("- ");
                for (int k = 0; k < artists.length; k++) {
                    allArtists.append(artists[k].getName());
                    if (k != artists.length - 1) {
                        allArtists.append(", ");
                    }
                }
                Label label = ((Label) currentScene.lookup("#bottomPreviewLabel" + i));
                if (!(label.getStyleClass().contains("text"))){
                    label.getStyleClass().add("text");
                }
                label.setText("  " + trackName + "\n  " + allArtists);
            }
            for (int i = size; i < 8; i++) {
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(true);
                node.setVisible(false);
            }

        }
        if (type == 4) {
            Paging<Track> tracks = App.session.getAppUser().getTrackPagingShort();
            int size = tracks.getItems().length;
            for (int i = 0; i < size; i++) {
                if (i == 8) {
                    break;
                }
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(false);
                node.setVisible(true);

                Track track = tracks.getItems()[i];
                Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 67, 67, false, false);
                ((ImageView) currentScene.lookup("#bottomPreviewImage" + i)).setImage(trackImage);
                String trackName = track.getName();
                ArtistSimplified[] artists = track.getArtists();
                StringBuilder allArtists = new StringBuilder("- ");
                for (int k = 0; k < artists.length; k++) {
                    allArtists.append(artists[k].getName());
                    if (k != artists.length - 1) {
                        allArtists.append(", ");
                    }
                }
                Label label = ((Label) currentScene.lookup("#bottomPreviewLabel" + i));
                if (!(label.getStyleClass().contains("text"))){
                    label.getStyleClass().add("text");
                }
                label.setText("  " + trackName + "\n  " + allArtists);
            }
            for (int i = size; i < 8; i++) {
                Node node = currentScene.lookup("#bottomPreview" + i);
                node.setDisable(true);
                node.setVisible(false);
            }

        }
    }
    public void buildTopPlaylist() {
        if (currentPlaylistEditState.equals("randomize")) {
            this.currentSelected.reorderItems();
            buildRandomization();
        }
        if (currentPlaylistEditState.equals("genre")) {
            executeGenreSplicing();
        }

    }
    public void executeGenreSplicing() {
        Playlist playlist;
        JSONObject json;
        try {
            json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
        String id = json.get("id").toString();
        CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(id, this.currentSplicedGenre)
                .public_(false)
                .description("Playlist created from the genre " + this.currentSplicedGenre)
                .build();
        try {
            playlist = createPlaylistRequest.execute();
            String[] uris = new String[this.genreSplicing.size()];
            for (int i = 0; i < this.genreSplicing.size(); i++) {
                uris[i] = this.genreSplicing.get(i).getUri();
            }
            SnapshotResult snapshotResult = spotifyApi
                    .addItemsToPlaylist(playlist.getId(), uris)
                    .build()
                    .execute();

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
        Pane pane = (Pane) currentScene.lookup("#genreSpliceBackground");
        pane.getStyleClass().remove("selected");

        clearTopPreview();
        if (playlist != null) {
            addPlaylistToChain(playlist);
        }
    }
    public void buildGenreSplicing() {
        GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi
                .getPlaylistsItems(this.currentSelected.playlistId)
                .limit(30)
                .build();

        Paging<PlaylistTrack> tracks;
        try {
            tracks = getPlaylistsItemsRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
        String genre = "";
        Random random = new Random();
        int randomGenre = random.nextInt(tracks.getItems().length);
        Track track = null;
        PlaylistTrack simpleTrack = tracks.getItems()[randomGenre];
        try {
            track = spotifyApi.getTrack(simpleTrack.getTrack().getId())
                    .build()
                    .execute();
            ArtistSimplified artistSimplified = track.getArtists()[0];
            Artist artist = this.spotifyApi.getArtist(artistSimplified.getId()).build().execute();
            if (artist.getGenres().length > 0) {
                genre = artist.getGenres()[0];
            }
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
        this.currentSplicedGenre = genre;

        ArrayList<Track> splicedGenreTracks = new ArrayList();
        for (int i = 0; i < tracks.getItems().length; i++) {
            String genreLocated = "";
            PlaylistTrack simple = tracks.getItems()[i];
            try {
                track = spotifyApi.getTrack(simple.getTrack().getId())
                        .build()
                        .execute();
                ArtistSimplified artistSimplified = track.getArtists()[0];
                Artist artist = this.spotifyApi.getArtist(artistSimplified.getId()).build().execute();
                for (int j = 0; j < artist.getGenres().length; j++) {
                    genreLocated = artist.getGenres()[j];
                    if (genreLocated.equals(genre)) splicedGenreTracks.add(this.spotifyApi.getTrack(simple.getTrack().getId()).build().execute());
                }

            } catch (IOException | ParseException | SpotifyWebApiException e) {
                throw new RuntimeException(e);
            }

        }
        int size = splicedGenreTracks.size();
        for (int i = 0; i < size; i++) {
            if (i == 8) {
                break;
            }
            Node node = currentScene.lookup("#topPreview" + i);
            node.setDisable(false);
            node.setVisible(true);
            track = splicedGenreTracks.get(i);

            Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 67, 67, false, false);
            ((ImageView) currentScene.lookup("#topPreviewImage" + i)).setImage(trackImage);
            String trackName = track.getName();
            ArtistSimplified[] artists = track.getArtists();
            StringBuilder allArtists = new StringBuilder("- ");
            for (int k = 0; k < artists.length; k++) {
                allArtists.append(artists[k].getName());
                if (k != artists.length - 1) {
                    allArtists.append(", ");
                }
            }
            Label label = ((Label) currentScene.lookup("#topPreviewInfo" + i));
            if (!(label.getStyleClass().contains("text"))){
                label.getStyleClass().add("text");
            }
            label.setText("  " + trackName + "\n  " + allArtists);
        }
        for (int i = size; i < 8; i++) {
            Node node = currentScene.lookup("#topPreview" + i);
            node.setDisable(true);
            node.setVisible(false);
        }
        this.genreSplicing = splicedGenreTracks;
    }


    public void buildRandomization() {

        GetPlaylistsItemsRequest getPlaylistsItemsRequest = spotifyApi
                .getPlaylistsItems(this.currentSelected.playlistId)
                .limit(8)
                .build();

        Paging<PlaylistTrack> tracks;
        try {
            tracks = getPlaylistsItemsRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }
        int size = tracks.getItems().length;
        for (int i = 0; i < size; i++) {
            if (i == 8) {
                break;
            }
            Node node = currentScene.lookup("#topPreview" + i);
            node.setDisable(false);
            node.setVisible(true);
            PlaylistTrack simpleTrack = tracks.getItems()[i];
            Track track = null;
            try {
                track = spotifyApi.getTrack(simpleTrack.getTrack().getId())
                        .build()
                        .execute();
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                throw new RuntimeException(e);
            }

            Image trackImage = new Image(track.getAlbum().getImages()[0].getUrl(), 67, 67, false, false);
            ((ImageView) currentScene.lookup("#topPreviewImage" + i)).setImage(trackImage);
            String trackName = track.getName();
            ArtistSimplified[] artists = track.getArtists();
            StringBuilder allArtists = new StringBuilder("- ");
            for (int k = 0; k < artists.length; k++) {
                allArtists.append(artists[k].getName());
                if (k != artists.length - 1) {
                    allArtists.append(", ");
                }
            }
            Label label = ((Label) currentScene.lookup("#topPreviewInfo" + i));
            if (!(label.getStyleClass().contains("text"))){
                label.getStyleClass().add("text");
            }
            label.setText("  " + trackName + "\n  " + allArtists);
        }
        for (int i = size; i < 8; i++) {
            Node node = currentScene.lookup("#topPreview" + i);
            node.setDisable(true);
            node.setVisible(false);
        }
    }
    public void handleEditSelection(MouseEvent event) {
        Pane pane = (Pane) currentScene.lookup("#randomizeBackground");
        Pane pane1 = (Pane) currentScene.lookup("#genreSpliceBackground");
        String[] sourceInfo = parseSource(event);
        if (!(currentPlaylistEditState.equals("")) && sourceInfo[1].toLowerCase().contains(currentPlaylistEditState)) {
            return;
        }
        if (sourceInfo[1].toLowerCase().contains("randomize")) {
            pane.getStyleClass().add("selected");
            pane1.getStyleClass().remove("selected");
            currentPlaylistEditState = "randomize";
            buildRandomization();
            return;
        }
        if (sourceInfo[1].toLowerCase().contains("genre")) {
            pane.getStyleClass().remove("selected");
            pane1.getStyleClass().add("selected");
            currentPlaylistEditState = "genre";
            buildGenreSplicing();
        }
    }
    public void handleSelection(MouseEvent event) {
        String[] sourceInfo = parseSource(event);
        if (!(currentPlaylistState.equals("")) && sourceInfo[1].toLowerCase().contains(currentPlaylistState)) {
            return;
        }
        Pane pane = (Pane) currentScene.lookup("#topartistsPane");
        Pane pane1 = (Pane) currentScene.lookup("#discoveryPane");
        Pane pane2 = (Pane) currentScene.lookup("#newreleasesPane");
        Pane pane3 = (Pane) currentScene.lookup("#topsongsPane");
        if (sourceInfo[1].toLowerCase().contains("topartists")) {
            pane.getStyleClass().add("selected");
            pane1.getStyleClass().remove("selected");
            pane2.getStyleClass().remove("selected");
            pane3.getStyleClass().remove("selected");
            currentPlaylistState = "topartists";
            loadBottomPreview(1);
            return;
        }
        if (sourceInfo[1].toLowerCase().contains("discovery")) {
            pane.getStyleClass().remove("selected");
            pane1.getStyleClass().add("selected");
            pane2.getStyleClass().remove("selected");
            pane3.getStyleClass().remove("selected");
            currentPlaylistState = "discovery";
            loadBottomPreview(2);
            return;
        }
        if (sourceInfo[1].toLowerCase().contains("newreleases")) {
            pane.getStyleClass().remove("selected");
            pane1.getStyleClass().remove("selected");
            pane2.getStyleClass().add("selected");
            pane3.getStyleClass().remove("selected");
            currentPlaylistState = "newreleases";
            loadBottomPreview(3);
            return;
        }
        if (sourceInfo[1].toLowerCase().contains("topsongs")) {
            pane.getStyleClass().remove("selected");
            pane1.getStyleClass().remove("selected");
            pane2.getStyleClass().remove("selected");
            pane3.getStyleClass().add("selected");
            currentPlaylistState = "topsongs";
            loadBottomPreview(4);

        }
    }




}
