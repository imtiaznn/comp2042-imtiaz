package com.comp2042.controller;

import java.awt.Point;
import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.logic.boards.Board;
import com.comp2042.logic.boards.SimpleBoard;
import com.comp2042.models.ClearRow;
import com.comp2042.models.ViewData;
import com.comp2042.view.GuiController;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.util.Duration;

import javafx.scene.input.KeyCode;

public class GameController implements InputEventListener {

    private Timeline timeline;
    private AnimationTimer animationTimer;
    private ClearRow lastClearRow;

    // Input smoothing
    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Map<KeyCode, Long> keyPressedAt = new HashMap<>();
    private final Map<KeyCode, Long> lastActionAt = new HashMap<>();    

    private static final long DAS_MS = 150;   // Initial delay before repeat
    private static final long ARR_MS = 50;    // Repeat interval while held
    private static final long SOFT_DROP_ARR_MS = 50; // Down key repeat rate

    // Game logic
    private static final long GAME_TICK = 400 * 1000000; // Game tick in miliseconds
    private long fallCooldown = 0;


    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c, Scene scene) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        attachKeyInput(scene);
        
        // Game loop insitialisation
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                processKeyInput();
                update(now);
            }
        };
        animationTimer.start();

        // Game loop initialization
        // timeline = new Timeline(new KeyFrame(
        //     Duration.millis(400),
        //     ae -> {
        //         processKeyInput();
        //         update();
        //     }
        // ));
        // timeline.setCycleCount(Timeline.INDEFINITE);
        // timeline.play();
        
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

    public void update(long now) {
        boolean canMove = true;

        // Refresh
        if (now - fallCooldown >= GAME_TICK) {
            canMove = board.moveBrickDown();
            fallCooldown = now;
        }
        
        // Move brick down
        viewGuiController.refreshBrick(board.getViewData());

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
                board.mergeBrickToBackground();
                break;
            default:
                break;
        }
        return board.getViewData();
    }

    // Apply DAS and ARR for key inputs
    public void attachKeyInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();

            if (!activeKeys.contains(code)) {
                activeKeys.add(code);

                long now = System.currentTimeMillis();
                keyPressedAt.put(code, now);
                lastActionAt.put(code, now);

                if (code == KeyCode.LEFT) onMoveEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
                else if (code == KeyCode.RIGHT) onMoveEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
                else if (code == KeyCode.UP) onMoveEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
                else if (code == KeyCode.C) onMoveEvent(new MoveEvent(EventType.HOLD, EventSource.USER));
                else if (code == KeyCode.DOWN) { onMoveEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
                    // Reset last action time for soft drop to apply immediately
                    // lastActionAt.put(code, now);
                }
                else if (code == KeyCode.SPACE) onMoveEvent(new MoveEvent(EventType.DROP, EventSource.USER));

                e.consume();                
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            activeKeys.remove(code);
            keyPressedAt.remove(code);
            lastActionAt.remove(code);
            e.consume();
        });
    }

    private void processKeyInput() {
    long now = System.currentTimeMillis();

        // Horizontal movement with DAS + ARR
        if (activeKeys.contains(KeyCode.LEFT)) {
            long pressed = keyPressedAt.getOrDefault(KeyCode.LEFT, now);
            long last = lastActionAt.getOrDefault(KeyCode.LEFT, 0L);
            if (now - pressed >= DAS_MS) {
                if (now - last >= ARR_MS) {
                    onMoveEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
                    lastActionAt.put(KeyCode.LEFT, now);
                }
            }
        }

        if (activeKeys.contains(KeyCode.RIGHT)) {
            long pressed = keyPressedAt.getOrDefault(KeyCode.RIGHT, now);
            long last = lastActionAt.getOrDefault(KeyCode.RIGHT, 0L);
            if (now - pressed >= DAS_MS) {
                if (now - last >= ARR_MS) {
                    onMoveEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
                    lastActionAt.put(KeyCode.RIGHT, now);
                }
            }
        }

        // Down key continuous (soft drop) with its own repeat rate
        if (activeKeys.contains(KeyCode.DOWN)) {
            // long pressed = keyPressedAt.getOrDefault(KeyCode.DOWN, now);
            long last = lastActionAt.getOrDefault(KeyCode.DOWN, 0L);
            if (now - last >= SOFT_DROP_ARR_MS) {
                onMoveEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
                lastActionAt.put(KeyCode.DOWN, now);
            }
        }
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}
