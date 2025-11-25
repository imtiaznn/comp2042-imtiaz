package com.comp2042.controller;

import java.awt.Point;

import com.comp2042.events.MoveEvent;
import com.comp2042.logic.boards.Board;
import com.comp2042.logic.boards.SimpleBoard;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;
import com.comp2042.view.GuiController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameController implements InputEventListener {

    private Timeline timeline;
    private ClearRow lastClearRow;

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        
        // Game loop initialization
        timeline = new Timeline(new KeyFrame(
            Duration.millis(400),
            ae -> update()
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        viewGuiController.bindScore(board.getScore().scoreProperty());
    }

    public ViewData getViewData() {
        return board.getViewData();
    }

    public ClearRow getClearRows() {
        ClearRow tmp = lastClearRow;
        lastClearRow = null;
        return tmp;
    }

    public void update() {
        // Trigger down event from the game loop
        // viewGuiController.moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        
        // Move brick down
        boolean canMove = board.moveBrickDown();
        // Update ghost position
        boolean ghostUpdated = board.updateGhostPosition();

        if (!canMove) {
            // Clear and merge rows
            board.mergeBrickToBackground();

            // Clear rows
            ClearRow clearRow = board.clearRows();
            lastClearRow = clearRow;

            // Update score
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                viewGuiController.showScoreNotification();
            }

            // End game if cannot move and final block is intersecting
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            
            // Refresh view
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            
        } else {
            board.getScore().add(1);
        }

        // Refresh
        viewGuiController.refreshBrick(board.getViewData());
    }

    @Override
    public void startGame() {
        if (timeline != null) timeline.play();
    }

    @Override
    public void stopGame() {
        if (timeline != null) timeline.stop();
    }

    public ViewData onMoveEvent(MoveEvent event) {
        switch (event.getEventType()) {
            case LEFT:
                board.moveBrickLeft();
                break;
            case RIGHT:
                board.moveBrickRight();
                break;
            case DOWN:
                board.moveBrickDown();
                break;
            case ROTATE:
                board.rotateLeftBrick();
                break;
            case HOLD:
                return board.holdBrick();
            case DROP:
                board.dropBrick();
            default:
                break;
        }
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
