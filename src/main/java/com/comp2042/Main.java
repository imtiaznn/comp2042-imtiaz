package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import com.comp2042.controller.GameController;
import com.comp2042.view.GuiController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getClassLoader().getResource("prstart.ttf").toExternalForm(), 38);
 
        // Loads the resource from FXML file and loads it
        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);

        // Loads the UI hierarchy (main menu controller handles navigation)
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 500, 482);

        primaryStage.setTitle("TetrisJFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
