/*
 * See README.md for information about this project
 */

package com.tcj.spui;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class App extends Application {

    public static final SpUIDatabase db;
    public static AppManager session;

    static {
        try {
            db = new SpUIDatabase();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    /*
     * Called via launch in main
     * Creates a new AppManager object that manages the App's components
     */
    @Override
    public void start(Stage initialStage)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {
        session = new AppManager();
        initiateLogin();
    }

    /*
     * Checks if MAC Address of user decrypts any refresh token (that is stored in the database) successfully
     * If it can, then user has already logged in on the machine being used and does not need to log in again (Like a 'Remember me' flag)
     * The decrypted refresh token is passed into the SpotifyAPI object and then is used to get a new access token
     * Load the main scene using StageManager and SceneManager, skipping logging
     *
     * If it cannot, then the user is prompted to log into Spotify, then encrypts the refresh token using their MAC address and stores in database
     * Load the login scene using StageManager and SceneManager, user then must accept the terms of using SpUI
     */
    public void initiateLogin()
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {
        String refreshToken = App.db.checkUsers();
        if (refreshToken != null) {
            App.session.getAppUser().getUserAuthorizationManager().getRetrievedApi()
                    .setRefreshToken(refreshToken);
            App.session.getAppUser().getUserAuthorizationManager().refreshAuthCode();
            App.session.loadAllAPIDependentStageSceneNetworks(); // once the authorization is complete we use the token from the SpotifyApi object in the UserAuthorizationManager to load all the information.
            App.session.getStageManager().retrieveStageSubNetworkWithKey("main").getParentStage().setScene(App.session.getStageManager().retrieveValidChildSceneFromStageParent("main", "homeScene"));
            App.session.getStageManager().retrieveStageSubNetworkWithKey("main").getParentStage().show();

        } else {
            App.session.getStageManager()
                    .buildAddStage("login", "Icon.png", "Login", true, false, StageStyle.UNDECORATED);
            App.session.getStageManager().retrieveStageSubNetworkWithKey("login")
                    .buildAddScene("loginScene", "login.fxml", "login_style.css");
            Stage loginStage = App.session.getStageManager()
                    .retrieveStageSubNetworkWithKey("login")
                    .getParentStage();
            loginStage.setScene(App.session.getStageManager()
                    .retrieveValidChildSceneFromStageParent("login", "loginScene"));
            loginStage.show();
        }
    }

}
