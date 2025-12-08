package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import java.io.IOException;

import com.comp2042.controller.GameController;

public class LevelSelectController {

    @FXML
    public void initialize() {

    }

    @FXML
    public void playGameNormal(ActionEvent event) {
        try {
            // Load the game layout directly for a normal game
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(gameRoot);

            // get the GUI controller instance created by FXMLLoader and start the game
            GuiController gui = loader.getController();
            // allow the GuiController to know the scene if needed
            gui.setScene(scene);
            new GameController(gui, scene);

        } catch (IOException e) {
            e.printStackTrace(); // check console for detailed error
        }
    }

    @FXML
    public void playGameTime(ActionEvent event) {
        try {
            // Load the game layout directly for a normal game
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(gameRoot);

            // get the GUI controller instance created by FXMLLoader and start the game
            GuiController gui = loader.getController();
            // allow the GuiController to know the scene if needed
            gui.setScene(scene);
            // Start Time Attack mode with a default duration (in seconds)
            int timeAttackDurationSeconds = 120; // 2 minutes default
            new GameController(gui, scene, timeAttackDurationSeconds);

        } catch (IOException e) {
            e.printStackTrace(); // check console for detailed error
        }
    }

    @FXML
    private void goMainMenu(ActionEvent event) {
        try {
            // load and switch to main menu
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent mainRoot = loader.load();
            ((Node) event.getSource()).getScene().setRoot(mainRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
