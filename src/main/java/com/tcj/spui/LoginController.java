package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private AnchorPane topBar;
    @FXML
    private WebView webView;
    private WebEngine engine;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button exitButton;

    private double barDragX;
    private double barDragY;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupWindowBarDrag();
        authorizeAndMoveToHome();
    }
    public void setupWindowBarDrag() {
        topBar.setOnMousePressed(event -> {
            barDragX = event.getSceneX();
            barDragY = event.getSceneY();
        });

        topBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) topBar.getScene().getWindow();
            stage.setX(event.getScreenX() - barDragX);
            stage.setY(event.getScreenY() - barDragY);
        });
    }
    public void authorizeAndMoveToHome() {
        engine = webView.getEngine();
        engine.load(App.session.getUserAuthorizationManager().getAuthorizationCodeRequestLink());
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code")+5, newValue.indexOf("state")-1);
                token = token.replaceAll("\\s.*", "");
                App.session.getUserAuthorizationManager().completeAuthorization(token);
                webView.getEngine().getLoadWorker().cancel(); // stop the listener
                App.session.loadAllAPIDependentStageSceneNetworks(); // once the authorization is complete we use the token from the SpotifyApi object in the UserAuthorizationManager to load all of the information.
                ((Stage)webView.getScene().getWindow()).close();
                Stage homeStage = App.session.getStageManager()
                        .retrieveStageSubNetworkWithKey("home")
                        .getParentStage();
                homeStage.setScene(App.session.getStageManager().retrieveStageSubNetworkWithKey("home").retrieveSceneWithKey("homeScene"));
                homeStage.show();
            }
        });
    }
    public void close() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void minimize() {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }
}
