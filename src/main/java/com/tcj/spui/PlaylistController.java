package com.tcj.spui;

public class PlaylistController extends SceneUtilities {
    public void initialize() {
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey)
                .getParentStage();
        // load info
        topBar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setupWindowBarDrag();
                this.currentScene = topBar.getScene();
                // display info
            }
        });
    }

    public void swapScene(){
        swapScene("homeScene", "Home");
    }
}
