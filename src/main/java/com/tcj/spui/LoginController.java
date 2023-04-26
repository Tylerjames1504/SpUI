package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class LoginController extends SceneUtilities {

    @FXML
    private WebView webView;

    public void initialize()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException { // initialize stage and scene if needed

        this.parentStage = App.session.getStageManager().retrieveStageSubNetworkWithKey("login")
                .getParentStage();


        setupWindowBarDrag();
        authorizeAndMoveToMainStage();


    }

    public void authorizeAndMoveToMainStage() {
        WebEngine engine = webView.getEngine();
        engine.load(
                App.session.getAppUser().getUserAuthorizationManager().getAuthorizationCodeRequestLink());
        engine.locationProperty().addListener((observable, oldValue, newValue) -> {
            this.currentScene = App.session.getStageManager()
                    .retrieveValidChildSceneFromStageParent("login", "loginScene");
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code") + 5,
                        newValue.indexOf("state") - 1);
                token = token.replaceAll("\\s.*", "");
                App.session.getAppUser().getUserAuthorizationManager().completeAuthorization(token);
                try {
                    JSONObject json = new JSONObject(App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getCurrentUsersProfile().build().getJson());
                    String userEmail = json.get("email").toString();
                    String authCode = App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getAccessToken();
                    String refreshToken = App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi().getRefreshToken();
                    App.db.initUser(userEmail, authCode, refreshToken);

                } catch (IOException | SpotifyWebApiException | ParseException |
                         InvalidAlgorithmParameterException | NoSuchPaddingException |
                         IllegalBlockSizeException | URISyntaxException | NoSuchAlgorithmException |
                         BadPaddingException | InvalidKeyException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                engine.getLoadWorker().cancel(); // stop the listener
                App.session.getStageManager().retrieveStageSubNetworkWithKey("login")
                        .buildAddScene("loading", "loading.fxml", "loading.css");
                App.session.getStageManager().retrieveStageSubNetworkWithKey("login")
                        .getParentStage()
                        .setScene(App.session.getStageManager().retrieveValidChildSceneFromStageParent("login", "loading"));
                this.parentStage.centerOnScreen();
                System.out.println("manual-start");
                App.session.loadAllAPIDependentStageSceneNetworks();
                System.out.println("manual-done");
                this.swapToStage(true, "main", "homeScene");
            }
        });
    }
}
