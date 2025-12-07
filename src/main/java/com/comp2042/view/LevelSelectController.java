package com.comp2042.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import com.comp2042.controller.GameSettings;
import com.comp2042.logic.boards.Board;
import com.comp2042.logic.boards.SimpleBoard;

import java.io.IOException;

public class LevelSelectController {

    @FXML private Button level1;
    @FXML private Button level2;
    @FXML private Button level3;
    @FXML private Button level4;
    @FXML private Button level5;
    @FXML private Button level6;
    @FXML private Button level7;
    @FXML private Button level8;
    @FXML private Button level9;
    @FXML private Button level10;
    @FXML private Button mainMenu;

    // required score to unlock levels (index 0 == level1)
    private final int[] unlockScores = {0, 100, 300, 600, 1000, 1500, 2100, 2800, 3600, 4500};


    @FXML
    public void initialize() {
        int playerScore = GameSettings.getPlayerScore();
        Button[] buttons = new Button[]{
            level1, level2, level3, level4, level5,
            level6, level7, level8, level9, level10
        };

        for (int i = 0; i < buttons.length; i++) {
            int required = unlockScores[i];
            Button b = buttons[i];
            if (playerScore < required) {
                b.setDisable(true);
                b.setTooltip(new Tooltip("Unlock at score: " + required));
            } else {
                b.setDisable(false);
                b.setTooltip(null);
            }
        }
    }

    // Called by each level button (onAction)
    @FXML
    private void onLevelSelected(ActionEvent event) {
        Button source = (Button) event.getSource();
        String text = source.getText(); // expected "Level N"
        int level = 1;
        try {
            level = Integer.parseInt(text.replaceAll("\\D", ""));
        } catch (NumberFormatException ignored) {}

        // store chosen level
        GameSettings.setSelectedLevel(level);

        // load game scene (gameLayout.fxml assumed to be existing)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("gameLayout.fxml"));
            Parent gameRoot = loader.load();

            // replace scene root first so the FXML controller's nodes are part of the scene
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(gameRoot);

            // get the controller instance created by FXMLLoader (do NOT new GuiController())
            final GuiController gui = loader.getController();
            
            // defer UI initialization until after layout pass
            Platform.runLater(() -> {
                new com.comp2042.controller.GameController(gui, scene);
            }); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent mainRoot = loader.load();
            ((Node) event.getSource()).getScene().setRoot(mainRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}