package com.tcj.spui;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    public static AppManager session;
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage initialStage) {
        session = new AppManager();
        initiateLogin();
        loadAllStageSceneNetworks();
    }
    public void loadAllStageSceneNetworks() { // loads all networks past the home page
    }
    public void initiateLogin() {
        App.session.getGuiManager().getStageManager().buildAddStage("login", "Icon.png", "Login",true, false, StageStyle.UNDECORATED);
        App.session.getGuiManager().getStageManager().retrieveStageSubNetworkWithKey("login").buildAddScene("loginScene", "login.fxml", "login_style.css");

        Stage loginStage = App.session.getGuiManager().getStageManager()
                .retrieveStageSubNetworkWithKey("login")
                .getParentStage();

        loginStage.setScene(App.session.getGuiManager().getStageManager().retrieveStageSubNetworkWithKey("login").retrieveSceneWithKey("loginScene"));

        loginStage.show();
    }

}
