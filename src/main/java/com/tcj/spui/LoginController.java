package com.tcj.spui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private WebView webView;
    private WebEngine engine;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webView.getEngine();
        engine.load(App.session.getAuthRequestLink());
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("callback?code=")) { //redirectURI must have /callback for this to work
                String token = newValue.substring(newValue.indexOf("code")+5, newValue.indexOf("state")-1);
                token = token.replaceAll("\\s.*", "");
                App.session.initiateApp(token);
                webView.getEngine().getLoadWorker().cancel(); // stop the listener
                ((Stage)webView.getScene().getWindow()).close();
                FXMLLoader homePage = new FXMLLoader(getClass().getResource("home_page.fxml"));
                Stage stage = new Stage();
                // moves to and creates Home scene and stage
                try { stage.setScene(new Scene(homePage.load()));}
                catch (IOException e) { throw new RuntimeException(e);}
                stage.getScene().getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("home_page_style.css")).toExternalForm());
                stage.centerOnScreen();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
                stage.setTitle("Home");
                stage.setResizable(false);
                stage.show();
            }
        });

    }
}
