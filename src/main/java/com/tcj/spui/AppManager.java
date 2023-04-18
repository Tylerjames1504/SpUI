package com.tcj.spui;

public class AppManager {
    final private UserAuthorizationManager userAuthorizationManager;
    final private StageManager stageManager;
    public AppManager() {
        this.userAuthorizationManager = new UserAuthorizationManager();
        this.stageManager = new StageManager();
    }
    public UserAuthorizationManager getUserAuthorizationManager() {
        return this.userAuthorizationManager;
    }
    public StageManager getStageManager() {
        return this.stageManager;
    }
    public void loadAllAPIDependentStageSceneNetworks() { // builds and sets up initial data for the scene -- threading?
        App.session.getStageManager().buildAddStage("home","Icon.png","Home",true,false,null);
        App.session.getStageManager().retrieveStageSubNetworkWithKey("home").buildAddScene("homeScene","home_page.fxml", "home_page_style.css");
    }

}
