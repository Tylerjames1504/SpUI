package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import se.michaelthelin.spotify.SpotifyApi;

public class SceneUtilities {
    @FXML
    public AnchorPane topBar;
    @FXML
    public Label minimizeButton;
    public double barDragX;
    public double barDragY;
    public Stage parentStage;
    public String parentStageKey;
    public Scene currentScene;
    public SpotifyUser appSpotifyUser = App.session.getAppUser();
    public SpotifyApi spotifyApi = appSpotifyUser.getUserAuthorizationManager().getRetrievedApi();

    public void swapScene(String sceneKey, String nextTitle) {
        if (nextTitle != null) parentStage.setTitle(nextTitle);
        parentStage.setScene(App.session.getStageManager().retrieveValidChildSceneFromStageParent(parentStageKey,sceneKey));
    }
    public void swapToStage(Boolean closeOldStage, String stageKey, String sceneKey) {
        if (closeOldStage) parentStage.close();
        Stage nextStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(stageKey).getParentStage();
        nextStage.setScene(App.session.getStageManager().retrieveValidChildSceneFromStageParent(stageKey,sceneKey));
        nextStage.show();
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

    public void setupWindowBarDrag() {
        topBar.setOnMousePressed(event -> {
            barDragX = event.getSceneX();
            barDragY = event.getSceneY();
        });
        topBar.setOnMouseDragged(event -> {
            parentStage.setX(event.getScreenX() - barDragX);
            parentStage.setY(event.getScreenY() - barDragY);
        });
    }
    public void close() { parentStage.close(); } // logout??
    public void minimize() { parentStage.setIconified(true); } // bug with button staying bold because of bad hover functionality




}

