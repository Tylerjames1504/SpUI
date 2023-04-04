package com.tcj.spui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage initialStage) throws IOException {
        Parent base = FXMLLoader.load(getClass().getResource("APP.fxml"));
        initialStage.setTitle("Authentication");
        initialStage.getIcons().add(new Image(getClass().getResourceAsStream("Icon.png")));
        initialStage.setScene(new Scene(base, 300,275));
        initialStage.show();
    }

    public static void main(String[] args) {
        Controller startProgram = new Controller();
        launch();
    }


}