package com.comp2042.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import com.comp2042.view.GuiController;
import com.comp2042.controller.GameController;

public class MainMenuController {

@FXML
public void playGame(ActionEvent event) {
        try {
            // Load the level selection screen so the user can choose Normal/Time Attack
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("levelSelect.fxml"));
            Parent root = loader.load();

            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace(); // check console for detailed error
        }
}

    @FXML
    public void exitGame(ActionEvent event) {
        Platform.exit();
    }
}
