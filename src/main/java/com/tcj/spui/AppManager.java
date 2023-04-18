package com.tcj.spui;

public class AppManager {
    private UserAuthorizationManager userAuthorizationManager;
    private StageManager stageManager;
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

}
