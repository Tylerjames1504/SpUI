package com.tcj.spui;

import javafx.application.Application;
import javafx.stage.Stage;

public class AppMain extends Application {
    public static AppManager session;
    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage initialStage) {
        session = new AppManager(initialStage);
    }
}
