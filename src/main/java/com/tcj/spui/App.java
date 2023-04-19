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
    }
    public void initiateLogin() {
        App.session.getStageManager().buildAddStage("login", "Icon.png", "Login",true, false, StageStyle.UNDECORATED);
        App.session.getStageManager().retrieveStageSubNetworkWithKey("login").buildAddScene("loginScene", "login.fxml", "login_style.css");
        Stage loginStage = App.session.getStageManager()
                .retrieveStageSubNetworkWithKey("login")
                .getParentStage();
        loginStage.setScene(App.session.getStageManager().retrieveValidChildSceneFromStageParent("login","loginScene"));
        loginStage.show();
    }

}
