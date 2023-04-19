package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.ArrayList;

public class SceneUtilities {
    @FXML
    public AnchorPane topBar;
    @FXML
    public Label minimizeButton;
    @FXML
    public Label exitButton;
    public double barDragX;
    public double barDragY;
    public Stage parentStage;
    public Scene currentScene;
    public User appUser = App.session.getAppUser();
    public SpotifyApi spotifyApi = appUser.getUserAuthorizationManager().getRetrievedApi();


    public void swapScene(Scene nextScene) {
        parentStage.setScene(nextScene);
    }
    public void swapToStage(Boolean closeOldStage, Stage nextStage, Scene nextScene) {
        if (closeOldStage) parentStage.close();
        nextStage.setScene(nextScene);
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
    public void setupWindowBarDrag() {
        topBar.setOnMousePressed(event -> {
            barDragX = event.getSceneX();
            barDragY = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() - barDragX);
            stage.setY(event.getScreenY() - barDragY);
        });
    }
    public void close() {
        parentStage.close();
    }

    public void minimize() {
        parentStage.setIconified(true);
    }


}

