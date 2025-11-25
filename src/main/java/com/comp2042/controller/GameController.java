package com.comp2042.controller;

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
            
            
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }
            
            // Refresh view
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            
        } else {
            board.getScore().add(1);
        }

        viewGuiController.refreshBrick(board.getViewData());
    }

    // Deprecated
    @Override
    public ViewData onDownEvent(MoveEvent event) {
        // boolean canMove = board.moveBrickDown();
        // if (!canMove) {
            
        //     board.mergeBrickToBackground();
        //     ClearRow clearRow = board.clearRows();
        //     lastClearRow = clearRow;

        //     if (clearRow.getLinesRemoved() > 0) {
        //         board.getScore().add(clearRow.getScoreBonus());
        //     }

        //     if (board.createNewBrick()) {
        //         viewGuiController.gameOver();
        //     }

        //     viewGuiController.refreshGameBackground(board.getBoardMatrix());

        // } else {
        //     if (event.getEventSource() == EventSource.USER) {
        //         board.getScore().add(1);
        //     }
        // }
        board.moveBrickDown();

        return board.getViewData();
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
            default:
                break;
        }
        return board.getViewData();
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        return board.holdBrick();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
