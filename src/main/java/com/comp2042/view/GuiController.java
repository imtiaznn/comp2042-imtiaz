package com.comp2042.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.text.View;

import com.comp2042.controller.InputEventListener;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int AMOUNT_NEXT_BRICKS = 3;

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

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] holdRectangles;
    private Rectangle[][] ghostRectangles;
    private Rectangle[][][] nextRectangles;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        // gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
        //     @Override
        //     public void handle(KeyEvent keyEvent) {
        //         // Arrow key movement
        //         if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
        //             if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //             if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //             if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //             if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.DOWN, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //             if (keyEvent.getCode() == KeyCode.C) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //             if (keyEvent.getCode() == KeyCode.SPACE) {
        //                 refreshBrick(eventListener.onMoveEvent(new MoveEvent(EventType.DROP, EventSource.USER)));
        //                 keyEvent.consume();
        //             }
        //         }

        //         // Start new game
        //         if (keyEvent.getCode() == KeyCode.N) {
        //             newGame(null);
        //         }
        //     }
        // });

        // Initialize game over panel
        gameOverPanel = new MessageOverlay("GAME OVER", "gameOverStyle", null);
        if (groupNotification != null) {
            groupNotification.getChildren().add(gameOverPanel);
            gameOverPanel.hide();
        }
    }

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

        brickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getxPosition() * BRICK_SIZE));
        brickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getyPosition() * BRICK_SIZE));

        ghostBrickPanel.setLayoutX(Math.round(gamePanel.getLayoutX() + brick.getGhostXPosition() * BRICK_SIZE));
        ghostBrickPanel.setLayoutY(Math.round(-40 + gamePanel.getLayoutY() + brick.getGhostYPosition() * BRICK_SIZE));

    }

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
                returnPaint = Color.DARKGREEN;
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
                    ghostRectangles[i][j].setFill(getFillColor(brick.getBrickData()[i][j]).deriveColor(0, 1, 1, 0.5));
                }
            }   

            refreshHold(brick);
            refreshNext(brick.getNextBricksData());
        }
    }

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

    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setStroke(null);
        rectangle.setSmooth(false);
    }

    public void showScoreNotification() {
        if (isPause.getValue() == Boolean.FALSE) {
            ClearRow clearRow = eventListener.getClearRows();

            if (clearRow != null && clearRow.getLinesRemoved() > 0) {
                // NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
                // groupNotification.getChildren().add(notificationPanel);
                // notificationPanel.showScore(groupNotification.getChildren());

                // Score notification
                MessageOverlay scoreNotification = new MessageOverlay("+" + clearRow.getScoreBonus(), "scoreNotificationStyle", Duration.seconds(1.0));
                groupNotification.getChildren().add(scoreNotification);
                scoreNotification.show();
            }
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
    }

    public void gameOver() {
        eventListener.stopGame();
        gameOverPanel.show();
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        eventListener.stopGame();
        gameOverPanel.hide();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        eventListener.startGame();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}
