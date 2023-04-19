package com.tcj.spui;
public class HomePageController extends SceneUtilities {
    public void initialize() {
        setupWindowBarDrag();
        this.parentStageKey = "main";
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey(parentStageKey).getParentStage();
    }
}
