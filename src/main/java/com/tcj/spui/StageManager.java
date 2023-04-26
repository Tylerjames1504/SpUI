package com.tcj.spui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/*
 * Class to manage Stages and their scenes
 * stageSet is a HashMap of Stage names to their respective SceneManager object
 * this map contains every scene that is used in the App
 */
public class StageManager {

    private final HashMap<String, SceneManager> stageSet = new HashMap<>();

    /*
     * Given a stageKey, which is a string that matches a Stage's name
     * return the SceneManager object that this string maps to in stageSet
     */
    public SceneManager retrieveStageSubNetworkWithKey(String stageKey) {
        return stageSet.get(stageKey);
    }

    /*
     * Given a stageKey, which is a string that matches a Stage's name
     * and a sceneKey, which is a string that matches a Scene's name that is a child of the stageKey Stage
     * return the Scene that the sceneKey maps to in sceneSetOfStageSet
     */
    public Scene retrieveValidChildSceneFromStageParent(String stageKey, String sceneKey) {
        return this.retrieveStageSubNetworkWithKey(stageKey).retrieveSceneWithKey(sceneKey);
    }

    /*
     * Uses flags that user passes in to create a new Stage
     * Stores this new Stage object in stageSet with its name as a key
     * Creates a new SceneManager that passes in the new Stage
     * Stage passed in will become the 'parent' stage of any Scene that is added into that SceneManager object
     *
     * In the end we get this relation which allows us to know which Stage is controlling which Scene(s)
     *
     * Using stageSet, grab a SceneManager object that contains many or one Scene objects that all have the same
     * parent Stage
     */
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

    /*
     * Component of the StageManager
     * Connects a parent Stage to all the Scenes it controls using sceneSetOfStageSet:HashMap
     * Connects Scene(s) to a parent Stage that controls it using parentStage:Stage
     */
    public static class SceneManager {

        private final HashMap<String, Scene> sceneSetOfStageSet = new HashMap<>();
        private final Stage parentStage;

        public SceneManager(Stage stage) {
            this.parentStage = stage;
        }

        public Stage getParentStage() {
            return this.parentStage;
        }

        /*
         * Called by StageManager's retrieveValidChildSceneFromStageParent method
         * Given a sceneKey which is a string that matches a Scene's name
         * return the Scene that the sceneKey maps to in sceneSetOfStageSet
         */
        public Scene retrieveSceneWithKey(String sceneKey) {
            return sceneSetOfStageSet.get(sceneKey);
        }

        /*
         * Given a CSS stylesheet, FXML sheet, and name
         * Loads the FXML sheet and creates a new Scene using the FXMLLoader class
         * If the FXMLLoader does not fail (fails when it cannot find the FXML sheet):
         * add the Scene into sceneSetOfStageSet with name as its key
         * Add the CSS stylesheet to the Scene to style it
         */
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
