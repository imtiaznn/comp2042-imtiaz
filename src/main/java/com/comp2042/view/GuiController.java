package com.comp2042.view;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.Scene;

import java.net.URL;
import java.util.ResourceBundle;

import com.comp2042.controller.InputEventListener;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**Controller class for managing the GUI of the Tetris game. */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int AMOUNT_NEXT_BRICKS = 3;

    @FXML  
    private BorderPane gameBoard;

    @FXML
    private GridPane gamePanel;

    @FXML
    private StackPane groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostBrickPanel;

    @FXML
    private MessageOverlay gameOverPanel;

    @FXML
    private GridPane holdPanel;

    @FXML
    private GridPane nextPanel;

    @FXML
    private VBox pauseOverlay;

    @FXML private Label scoreText;
    @FXML private Label levelText;
    @FXML private Label timerText;

    private Scene displayScene;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] holdRectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][][] nextRectangles;

    public final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override 
            public void handle(KeyEvent event) {
                // Toggles the game pause state
                if (event.getCode() == KeyCode.ESCAPE) {
                    if (isPause.getValue() == Boolean.FALSE) {
                        pauseGame();
                        event.consume();
                    } else {
                        resumeGame();
                        event.consume();
                    }
                }
            }
        });

        // Initialize game over panel
        gameOverPanel = new MessageOverlay("GAME OVER", "gameOverStyle", null);
        if (groupNotification != null) {
            groupNotification.getChildren().add(gameOverPanel);
            gameOverPanel.hide();
        }

        System.out.println("[GuiController] initialize id=" + System.identityHashCode(this) + " groupNotification=" + (groupNotification != null));

        // Score
        scoreText.setText("0");
    }

    /**
     * Initialize the game view with the given board matrix and brick data.
     * @param boardMatrix the initial game board matrix
     * @param brick the initial falling brick ViewData
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        // Create game board display
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        // Create falling brick display
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }

        // Create ghost brick display
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]).deriveColor(0, 1, 1, 0.5));
                ghostRectangles[i][j] = rectangle;
                ghostBrickPanel.add(rectangle, j, i);
            }
        }

        // Create hold brick display
        holdRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                holdRectangles[i][j] = rectangle;
                holdPanel.add(rectangle, j, i);
            }
        }

        // Create next brick display
        nextRectangles = new Rectangle[AMOUNT_NEXT_BRICKS][4][4];
        for (int n = 0; n < AMOUNT_NEXT_BRICKS; n++) {
           for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    nextRectangles[n][i][j] = rectangle;
                    // Multiply y position by 5 to create spacing between next bricks
                    nextPanel.add(rectangle, j, i + n * 5);
                }
            }
        }

        // Initial position of brick panels
        brickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE));
        brickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE));

        // Initial position of ghost panels
        ghostBrickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getGhostXPosition() * BRICK_SIZE));
        ghostBrickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getGhostYPosition() * BRICK_SIZE));

    }

    /**
     * Refresh the falling brick and ghost brick display.
     * @param brick the current ViewData containing brick information
     */
    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            // Set brick panel position
            brickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE));
            brickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE));
            
            // Set ghost panel position
            ghostBrickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getGhostXPosition() * BRICK_SIZE));
            ghostBrickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getGhostYPosition() * BRICK_SIZE));

            // Update current falling brick
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }

            // Update ghost brick
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], ghostRectangles[i][j]);
                    ghostRectangles[i][j].setFill(getFillColor(brick.getBrickData()[i][j]).deriveColor(0, 1, 1, 0.6));
                }
            }   

            refreshHold(brick);
            refreshNext(brick.getNextBricksData());
        }
    }

    /**
     * Refresh the hold brick display.
     * @param viewData the current ViewData containing held brick information
     */
    private void refreshHold(ViewData viewData) {
        int[][] held = viewData.getHeldBrickData();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int color = 0;
                if (i < held.length && j < held[i].length) {
                    color = held[i][j];
                }
                setRectangleData(color, holdRectangles[i][j]);
            }
        }
    }

    /**
     * Refresh the next bricks display.
     * @param nextBricks the array of next brick matrices
     */
    public void refreshNext(int[][][] nextBricks) {
        for (int n = 0; n < AMOUNT_NEXT_BRICKS; n++) {
            int[][] next = (nextBricks != null && nextBricks.length > n) ? nextBricks[n] : null;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int color = 0;
                    if (next != null && i < next.length && j < next[i].length) {
                        color = next[i][j];
                    }
                    setRectangleData(color, nextRectangles[n][i][j]);
                }
            }
        }
    }

    /**
     * Refresh the game background display based on the current board matrix.
     * @param board the current board matrix
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    /**
     * Set rectangle fill and stroke based on brick color code.
     * @param color the brick color code
     * @param rectangle the Rectangle to update
     */
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setStroke(null);
        rectangle.setSmooth(false);
    }

    /**
     * Show score notification when rows are cleared.
     * @param clearRow the ClearRow object containing information about cleared rows
     */
    public void showScoreNotification(ClearRow clearRow) {
        if (clearRow == null) return;
        if (isPause.getValue() == Boolean.FALSE && clearRow.getLinesRemoved() > 0) {
                System.out.println("[GuiController] showScoreNotification called with bonus=" + clearRow.getScoreBonus());
                // Score notification
                MessageOverlay scoreNotification = new MessageOverlay("+" + clearRow.getScoreBonus(), "scoreNotificationStyle", Duration.seconds(1.0));
                scoreNotification.setVisible(true);
                scoreNotification.setManaged(true);
                scoreNotification.setMouseTransparent(true);
                groupNotification.getChildren().add(scoreNotification);
                scoreNotification.toFront();
                scoreNotification.show();
                groupNotification.requestLayout();

                // Remove notification when it becomes hidden (MessageOverlay handles its own auto-hide)
                scoreNotification.visibleProperty().addListener((obsVis, oldVis, newVis) -> {
                    if (Boolean.FALSE.equals(newVis)) {
                        Platform.runLater(() -> {
                            groupNotification.getChildren().remove(scoreNotification);
                            System.out.println("[GuiController] scoreNotification removed");
                        });
                    }
                });
        }
        gamePanel.requestFocus();
    }

    /**
     * Set the event listener for input events.
     * @param eventListener the event listener to set
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Bind the score display to the given IntegerProperty.
     * @param integerProperty the property representing the current score
     */
    public void bindScore(javafx.beans.property.IntegerProperty integerProperty) {
        if (integerProperty == null || scoreText == null) return;
        // bind on FX thread to be safe
        javafx.application.Platform.runLater(() -> {
            scoreText.textProperty().bind(integerProperty.asString());
        });
    }

    /**
     * Bind the level display to the given IntegerProperty.
     * @param integerProperty the property representing the current level
     */
    public void bindTimer(javafx.beans.property.IntegerProperty integerProperty) {
        if (integerProperty == null || timerText == null) return;
        // update timer in mm:ss format whenever the property changes
        javafx.application.Platform.runLater(() -> {
            int seconds = integerProperty.get();
            int mins = seconds / 60;
            int secs = seconds % 60;
            timerText.setText(String.format("%02d:%02d", mins, secs));
        });

        integerProperty.addListener((obs, oldV, newV) -> {
            int s = newV.intValue();
            int m = s / 60;
            int sec = s % 60;
            javafx.application.Platform.runLater(() -> timerText.setText(String.format("%02d:%02d", m, sec)));
        });
    }

    /**
     * Go back to the main menu, stopping the current game if necessary.
     */
    public void goMainMenu(ActionEvent actionEvent) {
        // Stop the game before replacing the root
        if (eventListener != null) {
            eventListener.stopGame();
        }

        try {
            // Load the main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
            Parent mainMenuRoot = loader.load();

            // Replace the current scene's root with the main menu
            Scene currentScene = displayScene;
            currentScene.setRoot(mainMenuRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the current scene.
     * @param scene the scene to set
     */
    public void setScene(Scene scene) {
        this.displayScene = scene;
    }

    /**
     * Update the displayed score.
     * @param score the new score to display
     */
    public void updateScore(int score) {
        scoreText.setText(String.valueOf(score));
    }

    /**
     * Restart the current game by stopping it and creating a new game instance.
     */
    public void restartGame() {
        if (eventListener != null) {
            eventListener.stopGame();
            eventListener.createNewGame();
            eventListener.startGame();
            isPause.setValue(Boolean.FALSE);
            isGameOver.setValue(Boolean.FALSE);
            gameOverPanel.hide();
            pauseOverlay.setVisible(false);
            gamePanel.requestFocus();
        }
    }

    /**
     * Start a new game, stopping any existing game first.
     */
    public void newGame(ActionEvent actionEvent) {
        eventListener.stopGame();
        gameOverPanel.hide();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        eventListener.startGame();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    /**
     * 
     * Pause the current game.
     */
    public void pauseGame() {
        eventListener.stopGame();
        isPause.setValue(Boolean.TRUE);
        pauseOverlay.setVisible(true);
    }

    /**
     * Resume the paused game.
     */
    public void resumeGame() {
        eventListener.startGame();
        System.out.println("Resuming game...");        
        isPause.setValue(Boolean.FALSE);
        pauseOverlay.setVisible(false);
    }

    /**
     * Check if the game is currently paused.
     * @return true if the game is paused, false otherwise
     */
    public boolean isPause() {
        return isPause.getValue();
    }

    /**
     * Check if the game is over.
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return isGameOver.getValue();
    }

    /**
     * Show game over overlay with the round score and action buttons for replay and main menu.
     */
    public void gameOver(int score) {
        if (eventListener != null) eventListener.stopGame();

        gameOverPanel.setText("GAME OVER\n\nscore:" + score);
        // create action buttons
        javafx.scene.layout.HBox actions = new javafx.scene.layout.HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.control.Button playAgain = new javafx.scene.control.Button("Play Again");
        playAgain.setOnAction(ae -> {
            // restart the same game mode
            if (eventListener != null) {
                eventListener.createNewGame();
                eventListener.startGame();
            }
            gameOverPanel.hide();
            isGameOver.setValue(Boolean.FALSE);
            if (gamePanel != null) gamePanel.requestFocus();
        });

        javafx.scene.control.Button mainMenu = new javafx.scene.control.Button("Main Menu");
        mainMenu.setOnAction(ae -> {
            // stop and go back to main menu
            if (eventListener != null) eventListener.stopGame();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainMenu.fxml"));
                Parent mainMenuRoot = loader.load();
                if (displayScene != null) displayScene.setRoot(mainMenuRoot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        actions.getChildren().addAll(playAgain, mainMenu);
        actions.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Keep the overlay compact and center the buttons inside it
        gameOverPanel.setPrefHeight(160);
        gameOverPanel.setMaxHeight(160);
        BorderPane.setAlignment(actions, javafx.geometry.Pos.CENTER);
        BorderPane.setMargin(actions, new javafx.geometry.Insets(10, 0, 10, 0));
        gameOverPanel.setBottom(actions);

        gameOverPanel.show();
        isGameOver.setValue(Boolean.TRUE);
    }
    
    /**
     * Show game over overlay without score.
     */
    public void gameOver() {
        // default: show game over without score
        eventListener.stopGame();
        gameOverPanel.setText("GAME OVER");
        // remove any previous bottom controls
        gameOverPanel.setBottom(null);
        // show overlay
        gameOverPanel.show();
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Get the fill color for a given brick code.  
     * @param i brick code
     * @return the appropriate Color for the given brick code
     */
    private Color getFillColor(int i) {
        Color returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.GREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }

    // Getters
    public GridPane getGameBoardPanel() {
        return gamePanel;
    }
}