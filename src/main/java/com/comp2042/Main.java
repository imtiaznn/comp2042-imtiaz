package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Loads the resource from FXML file and loads it
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);

        // Loads the UI hierarchy and game controller
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        // App parameters
        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 400, 482);
        primaryStage.setScene(scene);
        primaryStage.show();
        new GameController(c);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
