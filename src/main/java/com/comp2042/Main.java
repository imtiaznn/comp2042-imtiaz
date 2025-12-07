package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import com.comp2042.controller.GameController;
import com.comp2042.view.GuiController;

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
        
        Scene scene = new Scene(root, 500, 482);
        c.setScene(scene);

        primaryStage.setTitle("TetrisJFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        new GameController(c, scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
