package com.tcj.spui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {
    public static AppManager session;
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage initialStage) {
        session = new AppManager();
        session.initializeGui();
        // generate all scenes here in memory to avoid generating while the program is running
        AppManager.GuiManager.StageManager.SceneManager temp = session.getGuiManager()
                .getStageManager()
                .retrieveStageSubNetworkWithKey("login");
        temp.getParentStage().setScene(temp.retrieveSceneWithKey("loginScene"));
        temp.getParentStage().show();

    }
}
