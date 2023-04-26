package com.tcj.spui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class StageManager {

    private final HashMap<String, SceneManager> stageSet = new HashMap<>();

    public SceneManager retrieveStageSubNetworkWithKey(String stageKey) {
        return stageSet.get(stageKey);
    }

    public Scene retrieveValidChildSceneFromStageParent(String stageKey, String sceneKey) {
        return this.retrieveStageSubNetworkWithKey(stageKey).retrieveSceneWithKey(sceneKey);
    }

    public void buildAddStage(String stageName, String iconPath, String title, Boolean centerOnScreen,
                              Boolean resizable, StageStyle style) {
        Stage newStage = new Stage();
        SceneManager stageChild = new SceneManager(newStage);
        stageSet.put(stageName, stageChild);
        newStage.getIcons()
                .add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(iconPath))));
        newStage.setTitle(title);
        if (centerOnScreen) {
            newStage.centerOnScreen();
        }
        if (!(resizable)) {
            newStage.setResizable(false);
        }
        if (style != null) {
            newStage.initStyle(style);
        }
    }

    public static class SceneManager {

        private final HashMap<String, Scene> sceneSetOfStageSet = new HashMap<>();
        private final Stage parentStage;

        public SceneManager(Stage stage) {
            this.parentStage = stage;
        }

        public Stage getParentStage() {
            return this.parentStage;
        }

        public Scene retrieveSceneWithKey(String sceneKey) {
            return sceneSetOfStageSet.get(sceneKey);
        }

        public void buildAddScene(String sceneName, String fxmlSheet, String styleSheet) {
            FXMLLoader loader;
            Scene newScene = null;

            try {
                loader = new FXMLLoader(getClass().getResource(fxmlSheet));
                newScene = new Scene(loader.load());
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
            sceneSetOfStageSet.put(sceneName, newScene);
            if (styleSheet != null) {
                newScene.getStylesheets().add(
                        Objects.requireNonNull(this.getClass().getResource(styleSheet)).toExternalForm());
            }
        }
    }

}
