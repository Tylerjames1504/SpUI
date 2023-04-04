package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private ClientManager session;
    public Controller() { this.session = new ClientManager(); }

    @FXML
    private WebView webView;
    private WebEngine engine;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webView.getEngine();
        engine.load(session.getAuthRequestLink());
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code")+5, newValue.indexOf("state")-1);
                token = token.replaceAll("\\s.*", "");
                session.initiateApp(token);
                webView.getEngine().getLoadWorker().cancel(); // stop the listener
                ((Stage)webView.getScene().getWindow()).close();
            }
        });
    }
}
