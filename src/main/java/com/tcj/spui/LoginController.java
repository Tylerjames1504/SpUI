package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
public class LoginController extends SceneUtilities {
    @FXML
    private WebView webView;
    public void initialize() { // initialize stage and scene if needed
        setupWindowBarDrag();
        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey("login").getParentStage();
        authorizeAndMoveToMainStage();
    }
    public void authorizeAndMoveToMainStage() {
        WebEngine engine = webView.getEngine();
        engine.load(App.session.getAppUser().getUserAuthorizationManager().getAuthorizationCodeRequestLink());
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code")+5, newValue.indexOf("state")-1);
                token = token.replaceAll("\\s.*", "");
                App.session.getAppUser().getUserAuthorizationManager().completeAuthorization(token);
                webView.getEngine().getLoadWorker().cancel(); // stop the listener
                ((Stage)webView.getScene().getWindow()).close();
                App.session.loadAllAPIDependentStageSceneNetworks(); // once the authorization is complete we use the token from the SpotifyApi object in the UserAuthorizationManager to load all the information.
                this.swapToStage(true, "main","homeScene");
            }
        });
    }
}
