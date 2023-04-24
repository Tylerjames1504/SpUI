package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class LoginController extends SceneUtilities {
    @FXML
    private WebView webView;
    public void initialize() { // initialize stage and scene if needed
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey("login").getParentStage();
        setupWindowBarDrag();
        authorizeAndMoveToMainStage();
    }
    public void authorizeAndMoveToMainStage() {
        WebEngine engine = webView.getEngine();
        engine.load(App.session.getAppUser().getUserAuthorizationManager().getAuthorizationCodeRequestLink());
        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            this.currentScene = App.session.getStageManager().retrieveValidChildSceneFromStageParent("login","loginScene");
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code")+5, newValue.indexOf("state")-1);
                token = token.replaceAll("\\s.*", "");
                App.session.getAppUser().getUserAuthorizationManager().completeAuthorization(token);
                engine.getLoadWorker().cancel(); // stop the listener
                this.parentStage.close();
                App.session.loadAllAPIDependentStageSceneNetworks(); // once the authorization is complete we use the token from the SpotifyApi object in the UserAuthorizationManager to load all the information.
                this.swapToStage(false, "main","homeScene");
            }
        });
    }
}
