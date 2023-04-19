package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.ArrayList;

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
    public User appUser = App.session.getAppUser();
    public SpotifyApi spotifyApi = appUser.getUserAuthorizationManager().getRetrievedApi();

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
            parentStage.setX(event.getScreenX() - barDragX);
            parentStage.setY(event.getScreenY() - barDragY);
        });
    }
    public void close() { parentStage.close(); } // logout??
    public void minimize() { // bug with button staying bold because of bad hover functionality
        parentStage.setIconified(true);
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
    public void bold(MouseEvent event) {
        String[] sourceInfo = parseSource(event.getSource());
        String type = sourceInfo[0];
        String fxid = sourceInfo[1];
        if (type.equals("Label")) {
            Label label = (Label) currentScene.lookup(fxid);
            label.setStyle("-fx-font-weight: bold");
        }
        if (type.equals("ToggleButton")) {
            ToggleButton toggleButton = (ToggleButton) currentScene.lookup(fxid);
            toggleButton.setStyle("-fx-font-weight: bold");
        }
        if (type.equals("Button")) {
            Button button = (Button) currentScene.lookup(fxid);
            button.setStyle("-fx-font-weight: bold");
        }

    }
    public void resetText(MouseEvent event) { //usage onMouseExited (add elements below if needed)
        String[] sourceInfo = parseSource(event.getSource());
        String type = sourceInfo[0];
        String fxid = sourceInfo[1];
        if (type.equals("Label")) {
            Label label = (Label) currentScene.lookup(fxid);
            label.setUnderline(false);
            label.setStyle("-fx-font-weight: normal");
        }
        if (type.equals("ToggleButton")) {
            ToggleButton toggleButton = (ToggleButton) currentScene.lookup(fxid);
            toggleButton.setUnderline(false);
            toggleButton.setStyle("-fx-font-weight: normal");
        }
        if (type.equals("Button")) {
            Button button = (Button) currentScene.lookup(fxid);
            button.setUnderline(false);
            button.setStyle("-fx-font-weight: normal");
        }
    }



}

