package com.comp2042.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import java.io.IOException;

public class MainMenuController {

@FXML
public void playGame(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/levelSelect.fxml"));
        Parent levelRoot = loader.load();
        Object ctrl = loader.getController();
        System.out.println("Loaded controller: " + ctrl);
        ((Node) event.getSource()).getScene().setRoot(levelRoot);
    } catch (IOException e) {
        e.printStackTrace(); // check console for detailed error
    }
}

    @FXML
    public void exitGame(ActionEvent event) {
        Platform.exit();
    }
}
