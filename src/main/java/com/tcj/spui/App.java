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

    @Override
    public void start(Stage initialStage)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, URISyntaxException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InterruptedException {
        session = new AppManager();
        initiateLogin();
    }

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
