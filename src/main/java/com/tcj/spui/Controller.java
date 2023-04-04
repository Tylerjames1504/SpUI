package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
        engine.load(session.authorizeUser());
        session.initiateApp();
    }
}
