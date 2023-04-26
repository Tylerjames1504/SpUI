package com.tcj.spui;

import javafx.stage.StageStyle;

/*
 * Class to manage App components
 * Manages the stages and their scenes of the App through a StageManager object
 * Stores a SpotifyUser object to easily access a User's Spotify information that is displayed
 */
public class AppManager {

    final private SpotifyUser appSpotifyUser;
    final private StageManager stageManager;

    public AppManager() {
        this.appSpotifyUser = new SpotifyUser();
        this.stageManager = new StageManager();
    }

    public SpotifyUser getAppUser() {
        return this.appSpotifyUser;
    }

    public StageManager getStageManager() {
        return this.stageManager;
    }

    /*
     * Easily add stages to the StageManager with certain flags passed in
     * These stages need to be initialized and created here due to their dependence on the Spotify Web API
     * This reduces load time *between scenes*, but increases the time it takes for the App to open
     */
    public void loadAllAPIDependentStageSceneNetworks() { // builds and sets up initial data for the scene -- threading?
        this.getStageManager()
                .buildAddStage("main", "Icon.png", "Home", true, false, StageStyle.UNDECORATED);
        this.getStageManager().retrieveStageSubNetworkWithKey("main")
                .buildAddScene("homeScene", "home_page.fxml", "home_page_style.css");
        this.getStageManager().retrieveStageSubNetworkWithKey("main")
                .buildAddScene("playlistScene", "playlist_page.fxml", "playlist_page_style.css");
    }

}
