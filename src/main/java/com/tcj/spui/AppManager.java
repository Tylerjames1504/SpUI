package com.tcj.spui;

import javafx.stage.StageStyle;

public class AppManager {
    final private User appUser;
    final private StageManager stageManager;
    public AppManager() {
        this.appUser = new User();
        this.stageManager = new StageManager();
    }
    public User getAppUser() { return this.appUser; }
    public StageManager getStageManager() {
        return this.stageManager;
    }
    public void loadAllAPIDependentStageSceneNetworks() { // builds and sets up initial data for the scene -- threading?
        this.getStageManager().buildAddStage("main","Icon.png","Home",true,false, StageStyle.UNDECORATED);
        this.getStageManager().retrieveStageSubNetworkWithKey("main").buildAddScene("homeScene","home_page.fxml", "home_page_style.css");
    }

}
