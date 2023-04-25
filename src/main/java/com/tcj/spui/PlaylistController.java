package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

import java.io.IOException;
import java.util.Objects;

public class PlaylistController extends SceneUtilities {
    @FXML
    ImageView rightButton;
    @FXML
    ImageView leftButton;
    private PlaylistBlockCycle playlistHead;


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
            }
        });
    }

    public void loadPlaylists() {
        // no playlist error --> incomplete pages too
        // refresh playlists using hashmap for genre and using playlist id
        // central storage of playlist chain may not be necessary as scenes using stage manager are never unloaded
        try {
            PlaylistBlockCycle head = new PlaylistBlockCycle();
            PlaylistBlockCycle temp = head;
            final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = App.session.getAppUser()
                    .getUserAuthorizationManager()
                    .getRetrievedApi()
                    .getListOfCurrentUsersPlaylists()
                    .build();
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = getListOfCurrentUsersPlaylistsRequest.execute();
            int k = 0;
            for (int i = 0; i < playlistSimplifiedPaging.getItems().length; i++) {
                PlaylistData tempData = new PlaylistData(playlistSimplifiedPaging.getItems()[i].getId());
                temp.block[k] = tempData;
                k++;
                if (k == 4) {
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

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println(e);
        }

    }
    public void swapScene(){
        swapScene("homeScene", "Home");
    }
    public void hoverArrowImage(MouseEvent event) {
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
    }
    public void movePage(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        if (sourceInfo[1].toLowerCase().contains("left")) {
            playlistHead = playlistHead.previous;
        }
        if (sourceInfo[1].toLowerCase().contains("right")) {
            playlistHead = playlistHead.next;
        }
    }


}
