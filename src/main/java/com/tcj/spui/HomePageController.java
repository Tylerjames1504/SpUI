package com.tcj.spui;
public class HomePageController extends SceneUtilities {
    public void initialize() {
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey).getParentStage();
        setupWindowBarDrag();
        topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                this.currentScene = topBar.getScene();
            }
        });
    }
}
